package com.billiards;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader.Validator;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Ball {
    public static final float RADIUS_PX = 10f;
    public static final float RADIUS_M = 0.25f;
    public static final float SCALE = 10f;
    public static final float SCALE_INV = 1/SCALE;
    public static final float RESTITUTION = 0.96f;
    private static final float FRICTION = 0.99f;
    private Sprite ballSprite;
    private Vector2 center;
    private Circle ballCircle;
    private Body ballBody;
    private boolean isMoving;

    public Ball(float initX, float initY, String fileName, Body body) { //, int ballNum) { //add after balls are properly implemented
        ballSprite = new Sprite(new Texture(fileName));
        center = new Vector2(initX, initY);
        ballBody = body;
        move(initX, initY);
    }

    public void move(float x, float y) {
        ballBody.setTransform(x * SCALE_INV, y * SCALE_INV, ballBody.getAngle());
        ballBody.setLinearVelocity(new Vector2(0, 0));
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
        float dst = ballBody.getLinearVelocity().dst(0, 0);
        //System.out.println(dst);
        if (dst * SCALE / 2 < 0.00047) { // scale was at 2
            ballBody.setLinearVelocity(0f, 0f);
            isMoving = false;
            return;
        }
        isMoving = true;
        float x = ballBody.getPosition().x * SCALE;
        float y = ballBody.getPosition().y * SCALE;
        ballSprite.setPosition(x - RADIUS_PX, y - RADIUS_PX);
        center.x = x;
        center.y = y;
        Vector2 vBall = ballBody.getLinearVelocity();
        ballBody.setLinearVelocity(vBall.scl(FRICTION));

        // if (dst > 0) {
        //     System.out.println(dst);
        // }

        // Keep in Bounds
        if (center.x - RADIUS_PX < 0 || center.x + RADIUS_PX > Billiards.WIDTH) {
            ballBody.setLinearVelocity(vBall.x * -FRICTION, vBall.y);
        }
        if (center.y - RADIUS_PX < 0 || center.y + RADIUS_PX > Billiards.HEIGHT) {
            ballBody.setLinearVelocity(vBall.x, vBall.y * -FRICTION);
        }
    }

    public void setVelocity(float vX, float vY) {
        ballBody.applyLinearImpulse(new Vector2(vX, vY), center, true);
        System.out.println("x: " + vX + " y: " + vY);
    }

    public boolean isMoving() {
        return isMoving;
    }

    public Vector2 getCenter() {
        return center;
    }

    public void setSpin() {

    }
}
