package br.com.rlmg.jokenpo.models;

import java.util.Date;

/**
 * Created by rlmg on 4/2/17.
 */

public class Match {

    private String id;
    private String player1;
    private String player2;
    private String player1Move;
    private String player2Move;
    private boolean playing;
    private String winner;
    private Date createdAt;

    public Match() {
    }

    public Match(String id, String player1, String player2, String player1Move, String player2Move, boolean
            playing, String winner, Date createdAt) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.player1Move = player1Move;
        this.player2Move = player2Move;
        this.playing = playing;
        this.winner = winner;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getPlayer1Move() {
        return player1Move;
    }

    public void setPlayer1Move(String player1Move) {
        this.player1Move = player1Move;
    }

    public String getPlayer2Move() {
        return player2Move;
    }

    public void setPlayer2Move(String player2Move) {
        this.player2Move = player2Move;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
