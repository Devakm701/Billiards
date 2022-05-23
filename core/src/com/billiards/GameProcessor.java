package com.billiards;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Represents the logic for an 8-Ball Pool game
 */
public class GameProcessor {
    private String ballType;
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
        System.out.println(turn.getName());
    }

    /**
     * Main game logic for
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
                } else if (numTurns > 2) {
                    boolean solid = num < 8 && num != 0;
                    turn.setType(solid); //is there autoboxing
                    getOppositePlayer(turn).setType(!solid);
                    if (solid) {
                        solids = turn;
                        stripes = getOppositePlayer(turn);
                    } else {
                        stripes = turn;
                        solids = getOppositePlayer(turn);
                    }
                    for (Ball ball : stripesOut) {
                        stripes.ballPotted(ball);
                    }
                    for (Ball ball : solidsOut) {
                        solids.ballPotted(ball);
                    }
                    System.out.println("Stripes: " + stripes.getName());
                } else {
                    if (num > 8) {
                        stripesOut.add(b);
                        if (turn == stripes) {
                            switchTurns = false;
                        }
                    } else {
                        solidsOut.add(b);
                        if (turn == solids) {
                            switchTurns = false;
                        }
                    }
                }
            } else if (num != 8 && num != 0) {
                if (num > 8) {
                    stripes.ballPotted(b);
                } else {
                    solids.ballPotted(b);
                }
            } else if (num == 8) {
                if (turn.is8BallAvailable()) {
                    billiardsGame.win(turn);
                } else {
                    billiardsGame.win(getOppositePlayer(turn));
                }
            }
        }
        if (switchTurns) {
            turn = getOppositePlayer(turn);
        }
        numTurns++;
        System.out.println(turn.getName());
    }

    public LinkedList<Ball> getStripesOutList() {
        return stripesOut;
    }

    public LinkedList<Ball> getSolidsOutList() {
        return solidsOut;
    }

    public Player getStripesPlayer() {
        return stripes;
    }

    public Player getSolidsPlayer() {
        return solids;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getTurn() {
        return turn;
    }

    public Player getOppositePlayer(Player turn) {
        if (turn == player1) {
            return player2;
        } else {
            return player1;
        }
    }
}
