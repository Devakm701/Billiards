package com.billiards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class PoolStick extends Sprite {
    private Vector2 origin;
    private boolean visible = true;
    private float chargeDist = 0f;
    public final float HEIGHT;
    public final float WIDTH;

    public PoolStick(String fileName, Vector2 origin) {
        super(new Texture(fileName));
        this.origin = origin; 
        HEIGHT = super.getHeight();
        WIDTH = super.getWidth();
        super.translate(origin.x - WIDTH - Ball.RADIUS_PX, origin.y - HEIGHT / 2);   
        
    }

    public PoolStick(String fileName, float x, float y) {
        this(fileName, new Vector2(x, y));
    }

    @Override
    public void draw(Batch batch) {
        if (!visible) {
            return;
        }
        super.draw(batch);
        if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
            chargeDist += 2f;
            super.setOrigin(WIDTH + Ball.RADIUS_PX + chargeDist, HEIGHT/2);
            super.setPosition(origin.x - chargeDist - getWidth() - Ball.RADIUS_PX, origin.y - HEIGHT / 2);
            super.draw(batch);
        }
        else if (chargeDist == 0 && Gdx.input.isTouched()) {
            super.setOrigin(WIDTH + Ball.RADIUS_PX, HEIGHT/2);
            super.setPosition(origin.x - WIDTH - Ball.RADIUS_PX, origin.y - HEIGHT / 2);
            super.setRotation((float)Math.toDegrees(Math.atan2(origin.y - Gdx.input.getY(), Gdx.input.getX() - origin.x)) + 180f);
            // System.out.println(super.getRotation()); // direction ouput
            super.draw(batch);
        } 
        else {
            chargeDist = Math.max(chargeDist - 7, 0);
            super.setOrigin(WIDTH + Ball.RADIUS_PX + chargeDist, HEIGHT/2);
            super.setPosition(origin.x - chargeDist - getWidth() - Ball.RADIUS_PX, origin.y - HEIGHT / 2);
            super.draw(batch);
        }
    }


    public void setVisible(boolean visibility) {
        visible = visibility;
    }

    public void move(float x , float y) {
        origin.x = x;
        origin.y = y;
        super.translate(y - WIDTH, y - HEIGHT / 2);  
    }

    
}