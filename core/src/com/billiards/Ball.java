package com.billiards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Represents a ball. Stores a sprite that can be drawn to the 
 * screen. Holds the body for the physics engine .Location and 
 * velocity can be updated. Can be moved using cursor or method.
 * @author Devak M
 * @version 5/23/22
 */
public class Ball implements Comparable<Ball>{
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
    private float lastAng = 0f;
    private Vector3 frontDir;
    float xRad = 0f;
    float yRad = 0f;

    
    /**
     * Constructs the ball with the necessary params
     * @param initX initial x coord
     * @param initY initial y coord
     * @param billiards main class for accessing methods
     * @param fileName filename for the shine of the ball
     * @param body Box2D body for the ball
     * @param ballNum numerical value of the ball
     * @param shadow Sprite that represents the shadow of the ball
     * @param instance 3D model of the ball
     */
    public Ball(float initX, float initY,Billiards billiards , String fileName, Body body, int ballNum, Sprite shadow, ModelInstance instance) { //add after balls are properly implemented
        ballShine = new Sprite(new Texture(fileName));
        ballShine.setAlpha(0.8f);
        center = new Vector2(initX, initY);
        ballBody = body;
        body.setLinearDamping(0.99f);
        body.setAngularDamping(0.5f);
        ballCircle = new Circle(initX, initY, RADIUS_PX);
        billiardsGame = billiards;
        ball3D = instance;
        move(initX, initY);
        this.ballNum = ballNum;
        this.shadow = shadow;
        
    }

    /**
     * Moves the ball to the desired x and y coordinates (IN PIXELS)
     * @param x coord in pixels
     * @param y coord in pixels
     */
    public void move(float x, float y) {
        ballBody.setTransform(x * SCALE_INV, y * SCALE_INV, ballBody.getAngle());
        // ballBody.setLinearVelocity(new Vector2(0, 0));
        ballShine.setPosition(x - RADIUS_PX, y - RADIUS_PX);
        ball3D.transform.setTranslation(x / 10f - 45f, y / 10f - 30f, 0);
        if (ballNum == 0){
            billiardsGame.movePoolStick(new Vector2(x, y));
        }
        center.x = x;
        center.y = y;
    }

    /**
     * Returns the sprite of the Ball
     * @return the balls sprite
     */
    public Sprite getSprite() {
        return ballShine;
    }

    /**
     * Returns a circle that represents the balls in 2D space
     * @return a Cirle object that holds the balls basic info
     */
    public Circle getCircle() {
        ballCircle.setPosition(center);
        return ballCircle;
    }

    /**
     * Updates the position of the ball, handles friction, interacts with the Box2D body, updates the 3D model, and allows the cue ball to be moveable
     * @return whether or not to remove the ball from the table
     */
    public boolean update() {
  
        if (!visible) {
            return false;
        }
        Vector2 vBall = ballBody.getLinearVelocity();
        float vAngle = ballBody.getAngularVelocity();
        isMoving = true;
        float x = ballBody.getPosition().x * SCALE;
        float y = ballBody.getPosition().y * SCALE;

        double ang = Math.atan2(y - center.y, x - center.x) + Math.PI / 2;
        float dist = center.dst(x, y);
        Vector3 axis = new Vector3((float)(Math.cos(ang)), (float)(Math.sin(ang)), 0);
        ball3D.transform.rotate(axis, (float) Math.toDegrees(dist / RADIUS_PX));
        ball3D.transform.setTranslation(x / 10f - 45f, y / 10f - 30f, 0);

        
        

        
        // ball3D.transform.rotate(0,0,1,(float)Math.toDegrees(ballBody.getAngle() - lastAng));
        // lastAng = ballBody.getAngle();
        // ballShine.setRotation((float)Math.toDegrees(ballBody.getAngle()));
        ballShine.setPosition(x - RADIUS_PX, y - RADIUS_PX);
        center.x = x;
        center.y = y;
        if (Math.abs(vBall.x) < LIMIT +  0.05 && Math.abs(vBall.y) < LIMIT+  0.05 && Math.abs(vAngle) < LIMIT +  0.05) {
            ballBody.setLinearVelocity(new Vector2(0, 0));
            ballBody.setAngularVelocity(0);
            isMoving = false;
        } else {
            // ballBody.setLinearVelocity(vBall.scl(FRICTION));
            ballBody.setAngularVelocity(vAngle * FRICTION);
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
                isMoving = true;
                if (dst < LIMIT * 2) {
                    move(hole.x, hole.y);
                    this.setVelocity(0, 0);
                    isMoving = false;

                    if (ballNum == 0) {
                        soundPlayed = false;
                    }
                    return true;
                }
                else {
                    ballBody.setLinearVelocity(new Vector2(hole.x, hole.y).sub(center).scl(0.5f));
                    isMoving = true;
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
                
                boolean outOfBounds = mouseX < 158 + RADIUS_PX ||  mouseX > 741 - RADIUS_PX ||
                    Billiards.HEIGHT - mouseY < 104 + RADIUS_PX || Billiards.HEIGHT - mouseY > 396 - RADIUS_PX;


                for (Ball b : billiardsGame.getBallList()) {
                    if (b.getNum() == 0) {
                        continue;
                    }
                    Vector2 bCenter = b.getCenter();
                    float dx = bCenter.x - mouseX;
                    float dy = bCenter.y - (Billiards.HEIGHT - mouseY);
                    if (dx*dx + dy*dy <  4 * RADIUS_PX * RADIUS_PX) {
                        return false;
                    }
                }
                if ((this.getCircle().contains(mouseX,Billiards.HEIGHT - mouseY) || leftPressed) && !outOfBounds) {
                    billiardsGame.getStick().setChargeDist(0f);
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
    
    /**
     * Getter for whether or not the ball is moveable
     * @return true if ball is moveable else false
     */
    public boolean isMoveable() {
        return moveable;
    }

    /**
     * Draws the shadow underneath the ball
     * @param batch batch to draw the shadow on
     */
    public void drawShadow(Batch batch) {
        
        Vector2 fromCenter = new Vector2(center.x - Billiards.TABLE_CENTER.x, center.y - Billiards.TABLE_CENTER.y);
        double dir = Math.atan2(fromCenter.y, fromCenter.x);
        float dst = fromCenter.dst(0,0)/334f * RADIUS_PX / 2;
        shadow.setPosition(dst * (float)Math.cos(dir) + center.x - shadow.getWidth() / 2,dst * (float)Math.sin(dir)+ center.y -  shadow.getHeight() / 2);
        shadow.draw(batch);
    }

    /**
     * Sets the velocity of the box2D body
     * @param vX x component of velocity
     * @param vY y component of velocity
     */
    public void setVelocity(float vX, float vY) {
        ballBody.setLinearVelocity(new Vector2(vX, vY));
        // System.out.println("x: " + vX + " y: " + vY);
    }

    /**
     * Getter method for whether or not the ball is moving
     * @return true if the ball is moving otherwise false
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Gets the location of the center of the ball
     * @return the center of the ball as a vector2 object
     */
    public Vector2 getCenter() {
        return center;
    }

    /**
     * Method for updating the visibility of a ball
     * @param visibility true for visible false to hide
     */
    public void setVisible(boolean visibility) {
        visible = visibility;
    }

    /**
     * Returns if ball is visible
     * @return true if visible false if hidden
     */
    public boolean isVisible(){
        return visible;
    }

    /**
     * Getter for the model of a Ball
     * @return the balls model as a modelinstance
     */
    public ModelInstance getModel() {
        return ball3D;
    }

    /**
     * Update whether or not the ball can be moved using the cursor
     * @param moveable true for can be moved false for cant be moved
     */
    public void setMoveable(boolean moveable) {
        this.moveable = moveable;
    }

    /**
     * Getter method for the ball number
     * @return the balls number
     */
    public int getNum() {
        return ballNum;
    }

    /**
     * Getter for the box2D body of a ball. Used for modifying the balls behavior in the box2D world
     * @return the balls body
     */
    public Body getBody() {
        return ballBody;
    } 

    /**
     * Used for updating when the ball is moving
     * @param moving true if the ball is moving false if the ball is not moving
     */
    public void setMoving(boolean moving) {
        moving = false;
    }

    /**
     * Converts the ball into a string
     * @return the string representation of the ball
     */
    public String toString() {
        return ballNum + "";
    }

    /**
     * Compares one ball to another
     * @param other The ball to compare to 
     * @return a negative number if this ball is smaller, 0 if this ball is equal and a positive number if this one is greater 
     */
    public int compareTo(Ball other) {
        return ballNum - other.ballNum;
    }

    /**
     * Checks if this ball is equal to another ball
     * @param other the Ball to check for equivalency
     * @return true if equal false if not equal
     */
    public boolean equals(Object other) {
        if (!(other instanceof Ball)) {
            return false;
        }
        return ((Ball)other).getNum() == ballNum;
    }
}
