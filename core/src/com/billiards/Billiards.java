package com.billiards;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Billiards extends ApplicationAdapter {
    SpriteBatch batch;
    Texture table, background;
    PoolStick stick;
    
    @Override
    public void create () {
        batch = new SpriteBatch();
        table = new Texture("stolenTableCropped.png");
        background = new Texture("dimmerBackground.png");
        stick = new PoolStick("pool stick.png", 450f , 300f);
    }

    @Override
    public void render () {
        ScreenUtils.clear(0, 0, 0.2f, 0);

        batch.begin();
        batch.draw(background, 0, 0);
        batch.draw(table, 450 - table.getWidth() / 2, 0);
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