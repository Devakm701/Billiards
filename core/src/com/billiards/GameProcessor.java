package com.billiards;

import java.util.LinkedList;

/**
 * Represents the game rules logic for an 8-Ball Pool game
 * @author Jeffrey L
 * @author Collaborators: Devak M, Roy Y
 * @version 2022 May 23
 */
public class GameProcessor {

    private Player turn;
    private Player player1;
    private Player player2;
    private Billiards billiardsGame;
    private LinkedList<Ball> stripesOut;
    private LinkedList<Ball> solidsOut;
    private Player stripes;
    private Player solids;
    private int numTurns;

    /**
     * Constructor that passes in the game 
     * @param billiards main game for accessing methods 
     */
    public GameProcessor(Billiards billiards) {
        this.billiardsGame = billiards;
        this.player1 = new Player("Player 1");
        this.player2 = new Player("Player 2");
        this.turn = player1;
        this.solidsOut = new LinkedList<>();
        this.stripesOut = new LinkedList<>();
        // System.out.println(turn.getName());
    }

    /**
     * Main game logic for 8-Ball Pool game and adds balls to their respective lists if potted
     * @param balls list of balls potted during turn
     */
    public void updateTurn(LinkedList<Ball> balls) {
        if (numTurns == 0) {
            numTurns++;
            return;
        }
        Boolean type = turn.getSolid();
        boolean switchTurns = true;
        for (Ball b : balls) {
            int num = b.getNum();
            if (num == 0) {
                billiardsGame.resetCueBall();
                billiardsGame.getCueBall().setVisible(true);
            }
            else if (type == null) {
                if (num == 8) {
                    billiardsGame.win(getOppositePlayer(turn));
                } else if (numTurns > 1) {
                    boolean solid = num < 8 && num != 0;
                    turn.setType(solid); //is there autoboxing
                    getOppositePlayer(turn).setType(!solid);
                    if (solid) {
                        solids = turn;
                        solids.ballPotted(b);
                        stripes = getOppositePlayer(turn);
                    } else {
                        stripes = turn;
                        stripes.ballPotted(b);
                        solids = getOppositePlayer(turn);
                    }
                    for (Ball ball : stripesOut) {
                        stripes.ballPotted(ball);
                    }
                    for (Ball ball : solidsOut) {
                        solids.ballPotted(ball);
                    }
                    if (balls.size() > 0) {
                        switchTurns = false; 
                    }
                    // System.out.println("Stripes: " + stripes.getName());
                } else {
                    if (num > 8) {
                        stripesOut.add(b);
                        switchTurns = false;
                        
                    } else if (num != 0 && num < 8){
                        solidsOut.add(b);
                        switchTurns = false;
                        
                    }
                }
            } else if (num != 8 && num != 0) {
                if (num > 8) {
                    stripes.ballPotted(b);
                    if (turn == stripes) {
                        switchTurns = false;
                    }
                } else {
                    solids.ballPotted(b);
                    if (turn == solids) {
                        switchTurns = false;
                    }
                }
                // switchTurns = false;
            } else if (num == 8) {
                if (turn.is8BallAvailable()) {
                    billiardsGame.win(turn);
                } else {
                    billiardsGame.win(getOppositePlayer(turn));
                }
            }
        }
        if (balls.contains(billiardsGame.getCueBall())) {
            for (Ball ball : balls) {
                if (ball.getNum() ==8) {
                    billiardsGame.win(getOppositePlayer(turn));
                }
            }
            switchTurns = true;
        }
        if (switchTurns) {
            turn = getOppositePlayer(turn);
        }
        numTurns++;
        // System.out.println(turn.getName());
    }

    /**
     * returns list of striped balls that are out
     * @return list of stripes that are out
     */
    public LinkedList<Ball> getStripesOutList() {
        return stripesOut;
    }

    /**
     * returns list of solid balls that are out
     * does not include 8 ball or cue ball
     * @return list of solids balls that are out
     */
    public LinkedList<Ball> getSolidsOutList() {
        return solidsOut;
    }

    /**
     * returns player that is assigned to stripes
     * @return player assigned to stripes
     */
    public Player getStripesPlayer() {
        return stripes;
    }

    /**
     * returns player that is assigned to solids
     * @return player assigned to solids
     */
    public Player getSolidsPlayer() {
        return solids;
    }

    /**
     * returns player 1
     * @return player 1
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * returns player 2
     * @return player 2
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * returns which the player whose turn it is
     * @return which player's turn
     */
    public Player getTurn() {
        return turn;
    }

    /**
     * helper method for game logic for turn, win, and assignment
     * @param turn which players turn
     * @return opposite player of player that has the turn
     */
    public Player getOppositePlayer(Player turn) {
        if (turn == player1) {
            return player2;
        } else {
            return player1;
        }
    }
}
