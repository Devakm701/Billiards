package com.billiards;

import java.util.LinkedList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.physics.box2d.*;

public class Billiards extends ApplicationAdapter {
    SpriteBatch batch;
    Texture table, background;
    PoolStick stick;
    //LinkedList<Ball> balls; // after ball is complete
    Ball ball;
    World world; // pool table width is ~20 times ball diameter, ball radius ~9-10 pixels, set ball radius to ~.25 meters in box2D & table width to ~10m 
    
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
        ball = new Ball(450, 300, "sphere-17_20x20.png", world.createBody(ballDef));

    }

    @Override
    public void render () {
        ScreenUtils.clear(0, 0, 0.2f, 0);

        batch.begin();
        batch.draw(background, 0, 0);
        batch.draw(table, 450 - table.getWidth() / 2, 0);
        ball.getSprite().draw(batch);
        stick.draw(batch);
        batch.end();
    }
    
    @Override
    public void dispose () {
        batch.dispose();
        table.dispose();
        background.dispose();
    }
}