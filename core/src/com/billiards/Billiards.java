package com.billiards;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
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
    // private LinkedList<Ball> balls; // after ball is complete
    private Ball cueBall;
    private World world; // pool table width is ~20 times ball diameter, ball radius ~9-10 pixels, set ball radius to ~.25 meters in box2D & table width to ~10m 
    private LaunchMenu launchMenu; // 1 px = .25 m
    private SettingsMenu settingsMenu;
    private Circle[] holes = new Circle[6];
    private long lastTime;
    //private static ShapeRenderer drawShape = new ShapeRenderer();
    

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
        stick = new PoolStick("pool stick.png", 450f , 300f);
       
        // Create Box2D world
        world = new World(new Vector2(0, 0), true);

        // Create ground for friction
        //FrictionJoint friction = new FrictionJoint(world, 0);
        // BodyDef sDef = new BodyDef();
        // sDef.type = BodyDef.BodyType.StaticBody;
        // sDef.position.set(0f, 0f);
        // Body tableSurface = world.createBody(sDef);
        // FixtureDef sFixture = new FixtureDef();
        // PolygonShape gameShape = new PolygonShape();
        // gameShape.setAsBox(-WIDTH * 0.25f, 0);
        // sFixture.shape = gameShape;
        // sFixture.density = 1f;
        // sFixture.friction = 1f;
        // tableSurface.createFixture(sFixture);
        // FrictionJointDef jointDef = new FrictionJointDef();
        // jointDef.maxForce = 1f;
        // jointDef.maxTorque = 1f; // idk why tf this doesnt work
        



        // Ball Creation
        // balls = new LinkedList<>();
        BodyDef ballDef = new BodyDef();
        FixtureDef fixDef = new FixtureDef();
        ballDef.type = BodyDef.BodyType.DynamicBody;
        CircleShape ballCircle = new CircleShape();
        ballCircle.setRadius(Ball.RADIUS_M);
        fixDef.shape = ballCircle;
        fixDef.restitution = Ball.RESTITUTION; // restitution is how much of the speed remains after a collision
        fixDef.density = 1f;
        fixDef.friction = 1f;
        Body ball = world.createBody(ballDef);
        ball.createFixture(fixDef);
        cueBall = new Ball(450, 300, "sphere-17_20x20.png", ball);
        stick.setCueBall(cueBall);


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
        
        // while (dt > 0 ) {
        //     world.step(PHYSICS_DT, 6, 2);
        //     cueBall.update();
        //     dt -= PHYSICS_DT;
        // }
        long currentTime = System.currentTimeMillis();
        world.step(currentTime - lastTime, 2, 2);
        cueBall.update();
        lastTime = currentTime;
        //world.step(Gdx.graphics.getDeltaTime(), 6, 2);
        batch.draw(background, 0, 0);
        batch.draw(table, 450 - table.getWidth() / 2, 0);
        cueBall.getSprite().draw(batch);
        stick.draw(batch);
        batch.end();
        
    }
    
    @Override
    public void dispose () {
        batch.dispose();
        table.dispose();
        background.dispose();
        stick.getTexture().dispose();
        cueBall.getSprite().getTexture().dispose();
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