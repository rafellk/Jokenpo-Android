package br.com.rlmg.jokenpo.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rlmg on 4/4/17.
 */

public class GsonMatch implements GsonConverter<Match>{

    private String playing;

    private String player1_move;

    private String player2_move;

    private String _id;

    private String player1;

    private String __v;

    private String created_at;

    private String player2;

    private String winner;

    public String getPlaying() {
        return playing;
    }

    public void setPlaying(String playing) {
        this.playing = playing;
    }

    public String getPlayer1_move() {
        return player1_move;
    }

    public void setPlayer1_move(String player1_move) {
        this.player1_move = player1_move;
    }

    public String getPlayer2_move() {
        return player2_move;
    }

    public void setPlayer2_move(String player2_move) {
        this.player2_move = player2_move;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        return "ClassPojo [playing = " + playing + ", player1_move = " + player1_move + ", player2_move = " + player2_move + ", _id = " + _id + ", player1 = " + player1 + ", __v = " + __v + ", created_at = " + created_at + ", player2 = " + player2 + ", winner = " + winner + "]";
    }

    @Override
    public Match convert() {
        Date date = null;
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            date = formatter.parse(created_at);
        } catch (ParseException exception) {
            return null;
        }

        return new Match(player1, player2, player1_move, player2_move, Boolean.parseBoolean(playing), winner, date);
    }
}
