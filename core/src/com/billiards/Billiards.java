package com.billiards;

import java.util.LinkedList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.physics.box2d.*;

public class Billiards extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture table, background;
    private final float PHYSICS_RATE = 1f / 200;
    private PoolStick stick;
    // private LinkedList<Ball> balls; // after ball is complete
    private Ball cueBall;
    private World world; // pool table width is ~20 times ball diameter, ball radius ~9-10 pixels, set ball radius to ~.25 meters in box2D & table width to ~10m 
    private OrthographicCamera cam;
    
    @Override
    public void create () {
        //balls = new LinkedList<>();
        batch = new SpriteBatch();
        table = new Texture("stolenTableCropped.png");
        background = new Texture("dimmerBackground.png");
        stick = new PoolStick("pool stick.png", 450f , 300f);
        world = new World(new Vector2(0, 0), true);
        BodyDef ballDef = new BodyDef();
        ballDef.type = BodyDef.BodyType.DynamicBody;
        cueBall = new Ball(450, 300, "sphere-17_20x20.png", world.createBody(ballDef));
        stick.setCueBall(cueBall);
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight() / 2f);
    }

    @Override
    public void render () {
        ScreenUtils.clear(0, 0, 0.2f, 0);

        batch.begin();
        float dt = Gdx.graphics.getDeltaTime() * 1000;
        
        while (dt > 0 ) {
            world.step(2, 6, 2);
            cueBall.update();
            dt -= 2;
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
    
}