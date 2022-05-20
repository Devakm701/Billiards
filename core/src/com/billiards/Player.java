package com.billiards;

import java.util.LinkedList;

public class Player {
    private Boolean solids = null;
    private LinkedList<Ball> ballsPotted;
    private boolean eightBallAvailable = false;
    
    public Player() {
        this.ballsPotted = new LinkedList<>();
    }

    public void setType(boolean solid) {
        this.solids = solid;
    }

    public Boolean getSolid() {
        return solids;
    }

    public void ballPotted(Ball b) {
        ballsPotted.add(b);
        if (ballsPotted.size() == 7) {
            eightBallAvailable = true;
        }
    }

    public boolean is8BallAvailable() {
        return eightBallAvailable;
    }
}