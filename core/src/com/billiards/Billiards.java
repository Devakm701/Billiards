package com.billiards;

import java.util.LinkedList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Billiards extends Game {
    private SpriteBatch batch;
    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;
    private Texture table, background;
    public final static float PHYSICS_DT = 16; // constant that can be reduced to increase the rate of the physics sim
    private PoolStick stick;
    private LinkedList<Ball> balls; // after ball is complete
    private Ball cueBall;
    private World world; // pool table width is ~20 times ball diameter, ball radius ~9-10 pixels, set ball radius to ~.25 meters in box2D & table width to ~10m 
    private LaunchMenu launchMenu; // 1 px = .25 m
    private SettingsMenu settingsMenu;
    private long lastTime;
    private static ShapeRenderer drawShape;
    private Circle[] holes = {
        new Circle(150, 404, 17),
        new Circle(749, 404, 17),
        new Circle(150, 96, 17),
        new Circle(749, 96, 17),
        new Circle(450, 410, 15),
        new Circle(450, 90, 15)
    };
    

    @Override
    public void create () {
        // Menu Initialization
        background = new Texture("dimmerBackground.png");
        launchMenu = new LaunchMenu(this, background);
        settingsMenu = new SettingsMenu(this, background);
        this.setScreen(launchMenu); // enable launch menu 
        this.getScreen().show();
        
        // Textures
        batch = new SpriteBatch();
        table = new Texture("stolenTableCropped.png");
        stick = new PoolStick(new Texture("pool stick.png"), 450f , 300f);

        // Create Box2D world
        world = new World(new Vector2(0, 0), true);
        
        // Shape Shenanigans
        drawShape = new ShapeRenderer();

        // Ball Creation
        balls = new LinkedList<>();
        BodyDef ballDef = new BodyDef();
        FixtureDef fixDef = new FixtureDef();
        ballDef.type = BodyDef.BodyType.DynamicBody;
        ballDef.bullet = true;
        CircleShape ballCircle = new CircleShape();
        System.out.println(Ball.RADIUS_M);
        ballCircle.setRadius(Ball.RADIUS_M);
        fixDef.shape = ballCircle;
        System.out.println(fixDef.restitution);
        fixDef.restitution = 0.75f; // restitution is how much of the speed remains after a collision
        //fixDef.friction = 0.1f;
        fixDef.friction = 0.1f;
        fixDef.density = 1f;
        Body ball = world.createBody(ballDef);
        ball.createFixture(fixDef);
        cueBall = new Ball(300, 250, "sphere-17_20x20.png", ball);
        stick.setCueBall(cueBall);
        
        int h = 1;
        int downShift = 0;
        for (int i = 0; i <= h && h <= 5; i++) {
            for (int j = 0; j <= i; j++) {
                Body tmpBall = world.createBody(ballDef);
                tmpBall.createFixture(fixDef);
                balls.add(new Ball(600 + i * 18, 250 + j * 20 - downShift, "sphere-17_20x20.png", tmpBall));
            }
            downShift += 10;
            h++;
        }

        // Initialize outline
        ChainShape border = new ChainShape();
        Vector2[] windowOutline = {
            new Vector2(0, 0),
            new Vector2(WIDTH * Ball.SCALE_INV, 0),
            new Vector2(WIDTH* Ball.SCALE_INV, HEIGHT* Ball.SCALE_INV),
            new Vector2(0, HEIGHT* Ball.SCALE_INV)
        };
        border.createLoop(windowOutline);
        BodyDef bd = new BodyDef();
        bd.type = BodyType.StaticBody;
        FixtureDef fd = new FixtureDef();
        fd.shape = border;
        fd.restitution = 1f;
        fd.density = 1f;
        fd.friction = 0f;
        Body outlineBody = world.createBody(bd);
        outlineBody.createFixture(fd);

        // Clear everything
        ballCircle.dispose();


        lastTime = System.currentTimeMillis();
    }

    @Override
    public void render () {
        //ScreenUtils.clear(0, 0, 0.2f, 0); // i forgot what this does
        if (this.getScreen() != null) {
            super.render();
            return;
        }
        
        batch.begin();
        long currentTime = System.currentTimeMillis();
        world.step(1/144f, 40, 40);
        lastTime = currentTime;
        batch.draw(background, 0, 0);
        batch.draw(table, 450 - table.getWidth() / 2, 0);
        cueBall.update();
        for (Ball ball : balls) {
            ball.update();
            ball.getSprite().draw(batch);
        }
        //world.step(Gdx.graphics.getDeltaTime(), 6, 2);

        cueBall.getSprite().draw(batch);
        stick.draw(batch);
        batch.end();
        drawShape.begin(ShapeType.Line);
        drawShape.setColor(new Color(1f,0,0,0.01f));
        if (Gdx.input.isButtonPressed(Buttons.RIGHT)) { // creating debug lines for holes and walls of pool table
            // Draw Hole Debug Circle
            // drawShape.circle(150, 404, 17); // top left
            // drawShape.circle(749, 404, 17); // top right
            // drawShape.circle(150, 96, 17); // bottom left
            // drawShape.circle(749, 96, 17); // bottom right
            // drawShape.circle(450, 410, 15); // top middle
            // drawShape.circle(450, 90, 15); // bottom middle

            // Draw Table Debug Borders
            drawShape.line(183, 396, 430, 396); // top left
            drawShape.line(468, 396, 715, 396); // top right
            drawShape.line(183, 104, 430, 104); // bottom left
            drawShape.line(468, 104, 715, 104); // bottom right
            drawShape.line(158, 128, 158, 372); // mid left
            drawShape.line(741, 128, 741, 372); // mid right
            
            // top right corner
            drawShape.line(715, 396, 748, 429); 
            drawShape.line(741, 372, 773, 404);
            drawShape.line(748, 429,773, 404);

            // top middle corner
            drawShape.line(430, 396, 435, 410); // to wooden part on the left side
            drawShape.line(435, 410, 435, 426); // vertical line on the left side
            drawShape.line(435, 426, 463, 426); // top vertical line
            drawShape.line(468, 396, 463, 410); // to wooden part on the right side
            drawShape.line(463, 410, 463, 426); // vertical line on the right side

            // top left corner (same order as the previous ones)
            drawShape.line(183, 396, 150, 429); 
            drawShape.line(158, 372, 126, 404);
            drawShape.line(126, 404, 150, 429);
            
            // draw starting ball positions
            int h = 1;
            int downShift = 0;
            for (int i = 0; i <= h && h <= 5; i++) {
                for (int j = 0; j <= i; j++) {
                    drawShape.circle(600 + i * 18, 250 + j * 20 - downShift, 10);
                }
                downShift += 10;
                h++;
            }
        }
        drawShape.end();
    }
    
    @Override
    public void dispose () {
        batch.dispose();
        table.dispose();
        background.dispose();
        stick.getTexture().dispose();
        cueBall.getSprite().getTexture().dispose();
        drawShape.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public void openSettings() {
        this.setScreen(settingsMenu);
    }

    public void openLaunchMenu() {
        this.setScreen(launchMenu);
    }

    public void closeMenu() {
        this.setScreen(null);
        Gdx.input.setInputProcessor(null);
    }
}