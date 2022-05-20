package com.billiards;

import java.util.LinkedList;

import com.badlogic.gdx.scenes.scene2d.ui.List;

public class Game {
    private String ballType;
    private Player turn;
    private Player player1;
    private Player player2;
    private Billiards billiardsGame;
    private LinkedList<Ball> stripesOut;
    private LinkedList<Ball> solidsOut;
    private int numTurns;

    public Game(Billiards billiards) {
        this.billiardsGame = billiards;
        this.player1 = new Player();
        this.player2 = new Player();
        this.turn = player1;

    }

    public void updateTurn(LinkedList<Ball> balls, boolean didFoul) {
        Boolean type = turn.getSolid();
        for (Ball b : balls) {

            if (b.getNum() == 0) {
               billiardsGame.cueBallMovable(); 
            }
            if (type == null && numTurns > 1) {
                if (b.getNum() == 8) {
                    billiardsGame.win(getOppositePlayer(turn));
                } else {
                    boolean solid = b.getNum() < 8 && b.getNum() != 0;
                    turn.setType(solid);
                    getOppositePlayer(turn).setType(!solid);
                }

            } else if (b.getNum() != 8 && b.getNum() != 0) {

            } else if (b.getNum() == 8) {
                if (turn.is8BallAvailable()) {
                    billiardsGame.win(turn);
                } else {
                    billiardsGame.win(getOppositePlayer(turn));
                }
            }
        }
        numTurns++;
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
