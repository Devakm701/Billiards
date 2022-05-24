package com.billiards;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Represents the score, assigned ball, and data for a player
 */
public class Player {
    private Boolean solids = null;
    private LinkedList<Ball> ballsPotted;
    private boolean eightBallAvailable = false;
    private String playerName;
    
    /**
     * Constructor that passes in player name
     * @param name name of player
     */
    public Player(String name) {
        this.ballsPotted = new LinkedList<>();
        playerName = name;
    }

    /**
     * Sets the assigned ball for player. 
     * @param solid True is solids. False is stripes.
     */
    public void setType(boolean solid) {
        this.solids = solid;
    }

    /**
     * Returns true if is assigned solids. Returns false if is assigned stripes.
     * @return the type as a boolean object, can be null, true or false
     */
    public Boolean getSolid() {
        return solids;
    }

    /**
     * Adds to a player's potted balls list. Contains logic determining if 8-Ball is next ball for the win
     * @param b ball that is potted
     */
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

    /**
     * returns whether or not 8-Ball is needed for the win
     */
    public boolean is8BallAvailable() {
        return eightBallAvailable;
    }

    /**
     * returns list of potted balls
     */
    public LinkedList<Ball> getBalls() {
        return ballsPotted;
    }

    /**
     * returns player name
     */
    public String getName() {
        return playerName;
    }
}