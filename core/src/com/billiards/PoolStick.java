package com.billiards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Represents the pool stick that is used to strike the cueball. 
 * It can spin by following the cursor. 
 * Holding left click will charge the ball which changes striking strength.
 * @author Devak M
 * @version 5/23/22
 */
public class PoolStick extends Sprite {
    /**
     * Height of the pool cue texture
     */
    public final float HEIGHT;
    /**
     * Width of the pool cue texture
     */
    public final float WIDTH;
    private final float MAX_CHARGE = 75f;
    private final Input in = Gdx.input;
    private Vector2 origin;
    private Ball cueBall;
    private Vector2 startPoint;
    private float chargeDist = 0f;
    private float outCharge = 0f;
    private boolean chargeAvailable = true;
    private boolean visible = true;
    private Billiards billiards;
    
    /**
     * Three param constructor that uses anti-aliasing to smooth pool stick. Sets the position of the pool stick to follow the ball. 
     * @param texture pool stick design
     * @param origin position of pool stick
     * @param game main game for accessing methods
     */
    public PoolStick(Texture texture, Vector2 origin, Billiards game) {
        super(texture);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear); // Rendering trick that smooths sharp pixelated edges
        this.origin = origin; 
        HEIGHT = super.getHeight();
        WIDTH = super.getWidth();
        super.translate(origin.x - WIDTH - Ball.RADIUS_PX, origin.y - HEIGHT / 2);   
        billiards = game;
    }

    /**
     * Two param constructor that moves the pool stick according to the ball.
     * @param texture pool stick design
     * @param game main game for accessing methods
     * @param x x coord to initialize pool stick
     * @param y y coord to initialize pool stick
     */
    public PoolStick(Texture texture, float x, float y, Billiards game) {
        this(texture, new Vector2(x, y), game);
    }

    /**
     * Overrides the draw method from the sprite and modifies it to draw the pool cue at the desired position
     */
    @Override
    public void draw(Batch batch) {
        /*---- Makes Pool Stick only visible when cue ball is not moving ----*/
        if (!visible || billiards.isOver()) {
            if (billiards.noBallsMoving && !cueBall.isMoving()) {
                setVisible(true);
                move(cueBall.getCenter());
                //drawCharged(0, batch);
            }
            return;
        }
        // float dx = cueBall.getCenter().x - Gdx.input.getX();
        // float dy = cueBall.getCenter().y - Gdx.input.getY();
        // if (dy*dy + dx*dx < Ball.RADIUS_PX * Ball.RADIUS_PX) {
        //     move(cueBall.getCenter());
        //     chargeDist = 0;
        //     return;
        // }
        
        // Used to check input
        boolean left = in.isButtonPressed(Buttons.LEFT);
        boolean right = in.isButtonPressed(Buttons.RIGHT);
        boolean altControl = Billiards.altControl;

        /*---- Checks if user wants to charge pool stick ----*/
        if ((altControl && left || !altControl && right) && chargeAvailable) {
            if (startPoint == null) { 
                startPoint = new Vector2(in.getX(), in.getY()); // creates a new startpoint if one doesnt already exist
            } else {
                Vector2 vect = new Vector2(in.getX(), in.getY());
                float dst = startPoint.dst(vect);
                double rot = vect.sub(startPoint).angleRad() + Math.toRadians(getRotation()) + Math.PI; 
                drawCharged((float) (dst * Math.cos(rot)), batch);
            }
        }
        /*---- Checks if user wants to rotate pool stick ----*/
        else if (chargeDist == 0) {
            startPoint = null;
            setVisible(!cueBall.isMoving());
            if (altControl || (!altControl && left)) {
                setOrigin(WIDTH + Ball.RADIUS_PX, HEIGHT/2);
                setPosition(origin.x - WIDTH - Ball.RADIUS_PX, origin.y - HEIGHT / 2);
                float rotation = (float)Math.toDegrees(Math.atan2(Billiards.HEIGHT-origin.y - in.getY(), in.getX() - origin.x));
                if (!Billiards.stickFlip) {
                    rotation += 180f;
                }
                setRotation(rotation);
                // System.out.println(super.getRotation()); // direction ouput
            }
            super.draw(batch);
        } 
        /*---- Resets stick upon stick release ---*/
        else {
            startPoint = null;
            drawCharged(chargeDist-7, batch);
            if (outCharge > 10 && chargeAvailable) {
                //System.out.println(outCharge / MAX_CHARGE); // debug code
                launchCueBall(outCharge / MAX_CHARGE);
            } 
            outCharge = 0f;
            chargeAvailable = false;
            if (chargeDist == 0) {
                chargeAvailable = true;
                setVisible(!cueBall.isMoving());
            }
        }
    }

    /**
     * Setter for distance
     * @param dist value to set distance to 
     */
    public void setChargeDist(float dist) {
        chargeDist = dist;
    }

    /**
     * Helper method for draw, draws a pool stick while its charged
     * @param dist distance away from default position
     * @param batch sprite batch to draw it on
     */
    private void drawCharged(float dist, Batch batch) {
        chargeDist = dist;
        chargeDist = Math.min(Math.max(chargeDist, 0), MAX_CHARGE );
        outCharge = Math.max(outCharge, chargeDist);
        super.setOrigin(WIDTH + Ball.RADIUS_PX + chargeDist, HEIGHT/2);
        super.setPosition(origin.x - chargeDist - getWidth() - Ball.RADIUS_PX, origin.y - HEIGHT / 2);
        super.draw(batch);
    }

    /**
     * Updates the visibility of the pool cue.
     * @param visibility set to true to show, false to hide
     */
    public void setVisible(boolean visibility) {
        visible = visibility;
    }

    /**
     * Getter for whether or not the pool cue is visible
     * @return pool cue visibility
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Moves the pool cue to the desired location
     * @param v the location to move the pool cue to as a vector
     */
    public void move(Vector2 v) {
        origin.x = v.x;
        origin.y = v.y;
        super.setOrigin(WIDTH + Ball.RADIUS_PX + chargeDist, HEIGHT/2);
        super.setPosition(origin.x - chargeDist - getWidth() - Ball.RADIUS_PX, origin.y - HEIGHT / 2);
    }

    /**
     * Initializes the cue ball of the pool cue. Used for launching the cue ball
     * @param ball the ball to be set as the cue ball
     */
    public void setCueBall(Ball ball) {
        cueBall = ball;
        move(cueBall.getCenter());
    }

    /**
     * Launches in the direction of the pool cue at the given magnitude
     * @param v magnitude of launch
     */
    private void launchCueBall(float v) {
        v = v*v*v;//(float)Math.pow(v, 3); // remaps v between 0 and 1 from linear to exponential curve, gives more natural feeling shots, lower b is closer to linear
        v *= 2000 * Ball.SCALE_INV;
        if (billiards.getTurnNum() == 1) {
            v*=4;
        }
        double rad = Math.toRadians(getRotation());
        billiards.playCueSound();
        cueBall.setMoveable(false);
        cueBall.setVelocity((float)( v * Math.cos(rad) ),(float)( v * Math.sin(rad) ) );
    }

}