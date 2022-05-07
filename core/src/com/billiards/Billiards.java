package com.billiards;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class Billiards extends Game {
    private SpriteBatch batch;
    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;
    private Texture table, background;
    private final float PHYSICS_DT = 16; // constant that can be reduced to increase the rate of the physics sim
    private PoolStick stick;
    // private LinkedList<Ball> balls; // after ball is complete
    private Ball cueBall;
    private World world; // pool table width is ~20 times ball diameter, ball radius ~9-10 pixels, set ball radius to ~.25 meters in box2D & table width to ~10m 
    private LaunchMenu launchMenu;
    private SettingsMenu settingsMenu;
    private Circle[] holes = new Circle[6];
    //private static ShapeRenderer shapinator = new ShapeRenderer();
    

    @Override
    public void create () {
        //balls = new LinkedList<>();
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
       
        // Physics Engine
        world = new World(new Vector2(0, 0), true);
        BodyDef ballDef = new BodyDef();
        ballDef.type = BodyDef.BodyType.DynamicBody;
        cueBall = new Ball(450, 300, "sphere-17_20x20.png", world.createBody(ballDef));
        stick.setCueBall(cueBall);
    }

    @Override
    public void render () {
        //ScreenUtils.clear(0, 0, 0.2f, 0); // i forgot what this does
        if (this.getScreen() != null) {
            super.render();
            return;
        }
        batch.begin();
        float dt = Gdx.graphics.getDeltaTime() * 1000;
        
        while (dt > 0 ) {
            world.step(PHYSICS_DT, 6, 2);
            cueBall.update();
            dt -= PHYSICS_DT;
        }
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
    }
}