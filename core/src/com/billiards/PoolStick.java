package com.billiards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class PoolStick extends Sprite {
    
    public final float HEIGHT;
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
    

    public PoolStick(Texture texture, Vector2 origin, Billiards game) {
        super(texture);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear); // Rendering trick that smooths sharp pixelated edges
        this.origin = origin; 
        HEIGHT = super.getHeight();
        WIDTH = super.getWidth();
        super.translate(origin.x - WIDTH - Ball.RADIUS_PX, origin.y - HEIGHT / 2);   
        billiards = game;
    }

    public PoolStick(Texture texture, float x, float y, Billiards game) {
        this(texture, new Vector2(x, y), game);
    }

    @Override
    public void draw(Batch batch) {
        /*---- Makes Pool Stick only visible when cue ball is not moving ----*/
        if (!visible) {
            if (!cueBall.isMoving()) {
                setVisible(true);
                move(cueBall.getCenter());
                //drawCharged(0, batch);
            }
            return;
        }
        
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
                if (!altControl) {
                    rotation += 180f;
                }
                setRotation(rotation);
                // System.out.println(super.getRotation()); // direction ouput
                super.draw(batch);
            }
        } 
        /*---- Resets stick upon stick release ---*/
        else {
            startPoint = null;
            drawCharged(chargeDist-7, batch);
            if (outCharge > 0 && chargeAvailable) {
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

    private void drawCharged(float dist, Batch batch) {
        chargeDist = dist;
        chargeDist = Math.min(Math.max(chargeDist, 0), MAX_CHARGE );
        outCharge = Math.max(outCharge, chargeDist);
        super.setOrigin(WIDTH + Ball.RADIUS_PX + chargeDist, HEIGHT/2);
        super.setPosition(origin.x - chargeDist - getWidth() - Ball.RADIUS_PX, origin.y - HEIGHT / 2);
        super.draw(batch);
    }

    public void setVisible(boolean visibility) {
        visible = visibility;
    }

    public void move(Vector2 v) {
        origin.x = v.x;
        origin.y = v.y;
        super.setOrigin(WIDTH + Ball.RADIUS_PX + chargeDist, HEIGHT/2);
        super.setPosition(origin.x - chargeDist - getWidth() - Ball.RADIUS_PX, origin.y - HEIGHT / 2);
    }

    public void setCueBall(Ball ball) {
        cueBall = ball;
        move(cueBall.getCenter());
    }

    private void launchCueBall(float v) {
        v = v*v;//(float)Math.pow(v, 3); // remaps v between 0 and 1 from linear to exponential curve, gives more natural feeling shots, lower b is closer to linear
        v *= 600 * Ball.SCALE_INV;
        double rad = Math.toRadians(getRotation());
        billiards.playCueSound();
        cueBall.setVelocity((float)( v * Math.cos(rad) ),(float)( v * Math.sin(rad) ) );
    }

}