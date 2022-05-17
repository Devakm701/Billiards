package com.billiards;

import java.util.LinkedList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Billiards extends Game {
    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;
    private static final float PHYSICS_DT = 1/144f;
    public static boolean altControl = true;
    public static boolean stickFlip = true;
    private float volume = 1f;
    private int FPS = 144;
    private long lastTime;
    private static ShapeRenderer drawShape;
    private SpriteBatch batch;
    private LaunchMenu launchMenu;
    private SettingsMenu settingsMenu;
    private Texture table, background;
    private PoolStick stick;
    private LinkedList<Ball> balls; 
    private Ball cueBall;
    private World world; 
    private Sound cushionSound;
    private Sound ballSound;
    private Sound cueSound;
    private Sound buttonClickSound;
    private Music akashProject; 
    private Screen lastScreen = launchMenu;
    private Stage stage;
    private Button settingsButton;
    private LinkedList<Ball> ballsOut;
    private int ballsMoving;
    

    public static Circle[] holes = {
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
        stick = new PoolStick(new Texture("pool stick.png"), 450f , 300f, this);

        // Create Box2D world
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());
        
        // Shape Shenanigans
        drawShape = new ShapeRenderer();

        // Ball Creation
        balls = new LinkedList<>();
        BodyDef ballDef = new BodyDef();
        FixtureDef fixDef = new FixtureDef();
        ballDef.type = BodyDef.BodyType.DynamicBody;
        ballDef.bullet = true;
        CircleShape ballCircle = new CircleShape();
        ballCircle.setRadius(Ball.RADIUS_M);
        fixDef.shape = ballCircle;
        fixDef.restitution = 0.75f; // restitution is how much of the speed remains after a collision
        //fixDef.friction = 0.1f;
        fixDef.friction = 0.01f;
        fixDef.density = 1f;
        Body ball = world.createBody(ballDef);
        ball.createFixture(fixDef);
        cueBall = new Ball(300, 250, "sphere-19_20x20.png", ball, 0);
        stick.setCueBall(cueBall);
        
        int num = 1;
        int h = 1;
        int downShift = 0;
        for (int i = 0; i <= h && h <= 5; i++) {
            for (int j = 0; j <= i; j++) {
                Body tmpBall = world.createBody(ballDef);
                tmpBall.createFixture(fixDef);
                String file = "sphere-17_20x20.png";
                if (num == 5) {
                    file = "sphere-11_20x20.png";
                } else if (num % 2 == 0) {
                    file = "sphere-00_20x20.png";
                }
                balls.add(new Ball(600 + i * 18, 250 + j * 20 - downShift, file, tmpBall, num));
                num++;
            }
            downShift += 10;
            h++;
        }

        // Initialize outline
        ChainShape border = new ChainShape();
        // Vector2[] windowOutline = {
        //     new Vector2(0, 0),
        //     new Vector2(WIDTH * Ball.SCALE_INV, 0),
        //     new Vector2(WIDTH* Ball.SCALE_INV, HEIGHT* Ball.SCALE_INV),
        //     new Vector2(0, HEIGHT* Ball.SCALE_INV)
        // };

        Vector2[] tableOutline = {
            new Vector2(183, 396),
            new Vector2(430, 396),
            new Vector2(435, 410),
            new Vector2(435, 426),
            new Vector2(463, 426),
            new Vector2(463, 410),
            new Vector2(468, 396),
            new Vector2(715, 396),
            new Vector2(748, 429),
            new Vector2(773, 404),
            new Vector2(741, 372),
            new Vector2(741, 128),
            new Vector2(773, 97),
            new Vector2(748, 70),
            new Vector2(715, 104),
            new Vector2(468, 104),
            new Vector2(463, 90),
            new Vector2(463,74),
            new Vector2(435, 74),
            new Vector2(435, 90),
            new Vector2(430, 104),
            new Vector2(183, 104),
            new Vector2(150, 71),
            new Vector2(126, 98),
            new Vector2(158, 128),
            new Vector2(158, 372),
            new Vector2(126, 404),
            new Vector2(150, 429),
        };
        for (int i = 0; i < tableOutline.length; i++) {
            tableOutline[i] = tableOutline[i].scl(Ball.SCALE_INV);
        }
        border.createLoop(tableOutline);
        BodyDef bd = new BodyDef();
        bd.type = BodyType.StaticBody;
        FixtureDef fd = new FixtureDef();
        fd.shape = border;
        fd.restitution = 1f;
        fd.density = 1f;
        fd.friction = 0f;
        Body outlineBody = world.createBody(bd);
        outlineBody.createFixture(fd);
        
        // Sounds
        cushionSound = Gdx.audio.newSound(Gdx.files.internal("cushionHit.wav"));
        ballSound = Gdx.audio.newSound(Gdx.files.internal("ballHit2.wav"));
        cueSound = Gdx.audio.newSound(Gdx.files.internal("cueHit.wav"));
        akashProject = Gdx.audio.newMusic(Gdx.files.internal("Akash Music Genesis Project.wav"));
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("DoubleClick.wav"));

        // Settings Button
        stage = new Stage();
        TextButtonStyle exitStyle = new TextButtonStyle();
        exitStyle.up = Billiards.getDrawable("menuIcon.png");
        exitStyle.over = Billiards.getDrawable("menuHover.png");
        exitStyle.down = Billiards.getDrawable("menuPress.png");
        settingsButton = new Button(exitStyle);
        settingsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
                lastScreen = null;
                openSettings();
                playButtonClick();
			}
        });
        
        settingsButton.setBounds(10, Billiards.HEIGHT - 30, 20, 20);
        stage.addActor(settingsButton);
        
        
        // Miscellaneous
        ballCircle.dispose();
        lastTime = System.currentTimeMillis();
        setFPS(FPS);
        ballsOut = new LinkedList<>();
        

    
    }

    @Override
    public void render () {
        if (this.getScreen() != null) {
            super.render();
            return;
        }
        
        batch.begin();
        world.step(PHYSICS_DT, 40, 40);
        // world.step(Math.min(Gdx.graphics.getDeltaTime(), 0.15f), 6, 2);
        batch.draw(background, 0, 0);
        batch.draw(table, 450 - table.getWidth() / 2, 0);
        cueBall.update();
        ballsMoving = 0;
        for (Ball ball : balls) {
            if (ball.update()) {
                removeBall(ball);
            }
            if (ball.isVisible()){
                ball.getSprite().draw(batch);
            }
            if (ball.isMoving()) {
                ballsMoving++;
            }
        }
        
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
            
            // bottom left corner
            drawShape.line(183, 104, 150, 71); 
            drawShape.line(158, 128, 126, 98); 
            drawShape.line(126, 98, 150, 71);
            
            // bottom right corner
            drawShape.line(715, 104, 748, 70); // left verti
            drawShape.line(741, 128, 773, 97);
            drawShape.line(748, 70, 773, 97);
            
            // bottom middle 
            drawShape.line(430, 104, 435, 90); // to wooden part on the left side
            drawShape.line(435, 90, 435, 74); // vertical line on the left side
            drawShape.line(435, 74, 463, 74); // bottom vertical line
            drawShape.line(468, 104, 463, 90); // to wooden part on the right side
            drawShape.line(463, 90, 463, 74); // vertical line on the right side




            
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
        long currentTime = System.currentTimeMillis();
        stage.act(PHYSICS_DT);
        stage.draw();
        lastTime = currentTime;
    }
    
    public float setVolume(float vol) {
        volume = vol;
        return vol;
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
        lastScreen = this.getScreen();
        this.setScreen(settingsMenu);
    }

    public void openLaunchMenu() {
        lastScreen = this.getScreen();
        this.setScreen(launchMenu);
    }

    public static Drawable getDrawable(String fileName) {
        return (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture(fileName))));
    }

    public void lastScreen() {
        Screen tmp = this.getScreen();
        this.setScreen(lastScreen);
        lastScreen = tmp;
    }

    public void setFPS(int fps) {
        FPS = fps;
        Gdx.graphics.setForegroundFPS(fps);
        // physicsDelta = 1f / FPS;
    }

    public int getFPS() {
        return FPS;
    }

    public void removeBall(Ball ball) {
        world.destroyBody(ball.getBody());
        ball.setVisible(false);
        if (ball != cueBall) {
            ballsOut.add(ball);
        }
    }

    public void closeMenu() {
        lastScreen = this.getScreen();
        this.setScreen(null);
        Gdx.input.setInputProcessor(stage);
    }

    public void playCueSound() {
        cueSound.play(volume);
    } // is there sound

    public void playAkashSound() {
        akashProject.setLooping(true);
        akashProject.play();
    }

    public void stopAkashSound() {
        akashProject.stop();
    }

    public Music getAkash() {
        return akashProject;
    }

    public void playButtonClick() {
        buttonClickSound.play(volume);
    }

    public Ball getCueBall() {
        return cueBall;
    }

    public LinkedList<Ball> getBallList() {
        return balls;
    }

    public Texture getStickTexture() {
        return stick.getTexture();
    }

    private class CollisionListener implements ContactListener {

        @Override
        public void beginContact(Contact contact) {
            if ((contact.getFixtureA().getShape() instanceof ChainShape) || (contact.getFixtureB().getShape() instanceof ChainShape)) {
                cushionSound.play(volume);
            } else {//if (contact.getFixtureA().getBody().getLinearVelocity() < V_MIN){
                ballSound.play(volume);
            }
        }

        @Override
        public void endContact(Contact contact) {}

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {}

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {}

    }

    public void destroyBody(Body body) {
        world.destroyBody(body);
    }
}