package com.billiards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.World;

public class Ball extends Circle {
    public static final float RADIUS_PX = 10f;
    public static final float RADIUS_M = 0.25f;

    public Ball(float initX, float initY, World world) { //, int ballNum, boolean isStripes) { //add after balls are properly implemented
        world = null;
    }
}
