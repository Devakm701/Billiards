package com.billiards;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Ball {
    public static final float RADIUS_PX = 10f;
    public static final float RADIUS_M = 0.25f;
    public static final float RESTITUTION = 0.96f;
    private Sprite ballSprite;
    private Vector2 center;
    private Circle ballCircle;
    private Body ballBody;

    public Ball(float initX, float initY, String fileName, Body body) { //, int ballNum) { //add after balls are properly implemented
        ballSprite = new Sprite(new Texture(fileName));
        center = new Vector2(initX, initY);
        ballBody = body;
        move(initX, initY);
        //ballBody.setLinearDamping(0.0005f); // shitty friction function if we ever become lazy
    }

    public void move(float x, float y) {
        ballBody.setTransform(x, y, ballBody.getAngle());
        ballSprite.setPosition(x - RADIUS_PX, y - RADIUS_PX);
        center.x = x;
        center.y = y;
    }

    public Sprite getSprite() {
        return ballSprite;
    }

    public Circle getCircle() {
        ballCircle.setPosition(center);
        return ballCircle;
    }

    public void update() {
        float x = ballBody.getPosition().x;
        float y = ballBody.getPosition().y;
        ballSprite.setPosition(x - RADIUS_PX, y - RADIUS_PX);
        center.x = x;
        center.y = y;
        // System.out.println(ballBody.getLinearVelocity());
    }

    public void setVelocity(float vX, float vY) {
        ballBody.applyLinearImpulse(new Vector2(vX, vY), center, true);
        System.out.println("x: " + vX + " y: " + vY);
    }


    public void setSpin() {

    }
}
