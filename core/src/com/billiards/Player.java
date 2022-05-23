package com.billiards;

import java.util.Collections;
import java.util.LinkedList;

public class Player {
    private Boolean solids = null;
    private LinkedList<Ball> ballsPotted;
    private boolean eightBallAvailable = false;
    private String playerName;
    
    public Player(String name) {
        this.ballsPotted = new LinkedList<>();
        playerName = name;
    }

    public void setType(boolean solid) {
        this.solids = solid;
    }

    public Boolean getSolid() {
        return solids;
    }

    public void ballPotted(Ball b) {
        if (ballsPotted.contains(b)) {
            return;
        }
        ballsPotted.add(b);
        if (ballsPotted.size() == 7) {
            eightBallAvailable = true;
        }
        Collections.sort(ballsPotted);
    }

    public boolean is8BallAvailable() {
        return eightBallAvailable;
    }

    public LinkedList<Ball> getBalls() {
        return ballsPotted;
    }

    public String getName() {
        return playerName;
    }
}