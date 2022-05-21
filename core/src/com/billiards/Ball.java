package com.billiards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Ball {
    public static final float RADIUS_PX = 10f;
    public static final float SCALE = 10f;
    public static final float SCALE_INV = 1/SCALE;
    public static final float RADIUS_M = RADIUS_PX * SCALE_INV;
    private static final float FRICTION = 0.995f;
    private static final float LIMIT = 0.25f;
    private Sprite ballShine;
    private Vector2 center;
    private Circle ballCircle;
    private Body ballBody;
    private boolean isMoving;
    private Billiards billiardsGame;
    private float timer = 0f;
    private final int ballNum;
    private boolean visible = true;
    private ModelInstance ball3D;
    private Sprite shadow;
    private boolean soundPlayed = false;
    private boolean moveable = false;
    private boolean leftPressed = false;

    

    public Ball(float initX, float initY,Billiards billiards , String fileName, Body body, int ballNum, Sprite shadow) { //add after balls are properly implemented
        ballShine = new Sprite(new Texture(fileName));
        center = new Vector2(initX, initY);
        ballBody = body;
        body.setLinearDamping(0.75f);
        body.setAngularDamping(0.5f);
        ballCircle = new Circle(initX, initY, RADIUS_PX);
        move(initX, initY);
        this.ballNum = ballNum;
        this.shadow = shadow;
        billiardsGame = billiards;
    }

    public void move(float x, float y) {
        ballBody.setTransform(x * SCALE_INV, y * SCALE_INV, ballBody.getAngle());
        ballBody.setLinearVelocity(new Vector2(0, 0));
        ballShine.setPosition(x - RADIUS_PX, y - RADIUS_PX);
        center.x = x;
        center.y = y;
    }

    public Sprite getSprite() {
        return ballShine;
    }

    public Circle getCircle() {
        ballCircle.setPosition(center);
        return ballCircle;
    }

    public boolean update() {
        // float dst = ballBody.getLinearVelocity().dst(0, 0);
        // //System.out.println(dst);
        // if (dst * SCALE / 2 == 0) {//< 0.00047) { // scale was at 2
        //     //ballBody.setLinearVelocity(0f, 0f);
        //     isMoving = false;
        //     return;
        // }
        if (!visible) {
            return false;
        }
        Vector2 vBall = ballBody.getLinearVelocity();
        float vAngle = ballBody.getAngularVelocity();
        isMoving = true;
        float x = ballBody.getPosition().x * SCALE;
        float y = ballBody.getPosition().y * SCALE;
        ballShine.setPosition(x - RADIUS_PX, y - RADIUS_PX);
        ballShine.setRotation((float)Math.toDegrees(ballBody.getAngle()));
        center.x = x;
        center.y = y;


        if (Math.abs(vBall.x) < LIMIT && Math.abs(vBall.y) < LIMIT && Math.abs(vAngle) < LIMIT) {
            ballBody.setLinearVelocity(new Vector2(0, 0));
            ballBody.setAngularVelocity(0);
            isMoving = false;
        } else {
            // ballBody.setLinearVelocity(vBall.scl(FRICTION));
            // ballBody.setAngularVelocity(vAngle * FRICTION);
        }
        //ballBody.setLinearVelocity(vBall.scl(FRICTION));
        //

        // Keep in Bounds
        // if (center.x - RADIUS_PX < 0 || center.x + RADIUS_PX > Billiards.WIDTH) {
        //     ballBody.setLinearVelocity(vBall.x * -1, vBall.y);
        // }
        // if (center.y - RADIUS_PX < 0 || center.y + RADIUS_PX > Billiards.HEIGHT) {
        //     ballBody.setLinearVelocity(vBall.x, vBall.y * -1);
        // }

        for (Circle hole : Billiards.holes) {
            if (hole.contains(center)) {
                float dst = center.dst(hole.x, hole.y); 
                // System.out.println(dst);
                if (dst < LIMIT * 2) {
                    move(hole.x, hole.y);
                    this.setVelocity(0, 0);
                    if (ballNum == 0) {
                        billiardsGame.resetCueBall();
                        soundPlayed = false;
                    }
                    return true;
                }
                else {
                    ballBody.setLinearVelocity(new Vector2(hole.x, hole.y).sub(center).scl(0.5f));
                    if (!soundPlayed) {
                        billiardsGame.playPocketSound();
                        soundPlayed = true;
                    }
                    
                }

            }
        } 

        if (moveable) {
            if (Gdx.input.isButtonPressed(Buttons.LEFT) || leftPressed){
                int mouseX = Gdx.input.getX();
                int mouseY = Gdx.input.getY();
                
                for (Ball b : billiardsGame.getBallList()) {
                    Vector2 bCenter = b.getCenter();
                    float dx = bCenter.x - mouseX;
                    float dy = bCenter.y - (Billiards.HEIGHT - mouseY);
                    if (dx*dx + dy*dy <  4 * RADIUS_PX * RADIUS_PX) {
                        return false;
                    }
                }
                if (this.getCircle().contains(mouseX,Billiards.HEIGHT - mouseY) || leftPressed) {
                    System.out.println("a");
                    move(mouseX, Billiards.HEIGHT - mouseY);
                    billiardsGame.setStickVisible(false);
                    leftPressed = true;
                }
                if (!Gdx.input.isButtonPressed(Buttons.LEFT)) {
                    leftPressed = false;
                }
            }
            else if (!isMoving){
                billiardsGame.movePoolStick(center);
                billiardsGame.setStickVisible(true);
            }
        } 
        return false;
    }
    
    public void drawShadow(Batch batch) {
        
        Vector2 fromCenter = new Vector2(center.x - Billiards.TABLE_CENTER.x, center.y - Billiards.TABLE_CENTER.y);
        double dir = Math.atan2(fromCenter.y, fromCenter.x);
        float dst = fromCenter.dst(0,0)/334f * RADIUS_PX / 2;
        shadow.setPosition(dst * (float)Math.cos(dir) + center.x - shadow.getWidth() / 2,dst * (float)Math.sin(dir)+ center.y -  shadow.getHeight() / 2);
        shadow.draw(batch);
    }

    public void setVelocity(float vX, float vY) {
        ballBody.setLinearVelocity(new Vector2(vX, vY));
        // System.out.println("x: " + vX + " y: " + vY);
    }

    public boolean isMoving() {
        return isMoving;
    }

    public Vector2 getCenter() {
        return center;
    }

    public void setVisible(boolean visibility) {
        visible = visibility;
    }

    public boolean isVisible(){
        return visible;
    }

    public void setMoveable(boolean moveable) {
        this.moveable = moveable;
    }

    public int getNum() {
        return ballNum;
    }

    public Body getBody() {
        return ballBody;
    } 

    public void setSpin() {

    }
}
