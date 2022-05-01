package com.billiards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class Ball {
    public static final float RADIUS_PX = 10f;
    public static final float RADIUS_M = 0.25f;
    public static final float RESTITUTION = 0.96f;
    private Sprite ballSprite;
    private Circle ballCircle;

    public Ball(float initX, float initY, String fileName, Body body) { //, int ballNum) { //add after balls are properly implemented
        ballSprite = new Sprite(new Texture(fileName));
        ballSprite.translate(initX - RADIUS_PX, initY - RADIUS_PX);
    }

    public void move() {

    }

    public Sprite getSprite() {
        return ballSprite;
    }

    public Circle getCircle() {
        
        return ballCircle;
    }
}
