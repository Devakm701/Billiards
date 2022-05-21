package com.billiards;

import java.util.HashMap;
import java.util.LinkedList;

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

    public GameProcessor(Billiards billiards) {
        this.billiardsGame = billiards;
        this.player1 = new Player("Player 1");
        this.player2 = new Player("Player 2");
        this.turn = player1;
        System.out.println(turn.getName());
    }

    /**
     * @param balls list of balls potted during turn
     */
    public void updateTurn(LinkedList<Ball> balls) {
        Boolean type = turn.getSolid();
        boolean switchTurns = true;
        for (Ball b : balls) {
            int num = b.getNum();
            if (num == 0) {
                billiardsGame.resetCueBall();
            }
            if (type == null) {
                if (num == 8) {
                    billiardsGame.win(getOppositePlayer(turn));
                } else if (numTurns > 1) {
                    boolean solid = num < 8 && num != 0;
                    turn.setType(solid);
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
        billiardsGame.turnUpdated = false;
        // System.out.println(turn.getName());
    }

    public LinkedList<Ball> getStripesOutList() {
        return stripesOut;
    }

    public LinkedList<Ball> getSolidsOutList() {
        return solidsOut;
    }

    public Player getOppositePlayer(Player turn) {
        if (turn == player1) {
            return player2;
        } else {
            return player1;
        }
    }
}
