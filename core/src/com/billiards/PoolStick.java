package com.billiards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class PoolStick extends Sprite {
    Vector2 origin;
    boolean visible = true;

    public PoolStick(String fileName, Vector2 origin) {
        super(new Texture(fileName));
        this.origin = origin; 
        super.translate(origin.x - super.getWidth(), origin.y - super.getHeight() / 2);   
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
        if (Gdx.input.isTouched()) {
            super.setOrigin(super.getWidth(), super.getHeight()/2);
            super.setRotation((float)Math.toDegrees(Math.atan2(origin.y - Gdx.input.getY(), Gdx.input.getX() - origin.x)) + 180f);
            // System.out.println(super.getRotation()); // direction ouput
            super.draw(batch);
        }
    }

    public void setVisible(boolean visibility) {
        visible = visibility;
    }

    public void move(float x , float y) {
        origin.x = x;
        origin.y = y;
        super.translate(y - super.getWidth(), y - super.getHeight() / 2);  
    }

    
}