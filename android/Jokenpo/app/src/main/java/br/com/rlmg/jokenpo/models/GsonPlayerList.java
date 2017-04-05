package br.com.rlmg.jokenpo.models;

/**
 * Created by rlmg on 4/4/17.
 */

public class GsonPlayerList implements GsonConverter {

    private GsonPlayer[] players;

    public GsonPlayer[] getPlayers() {
        return players;
    }

    public void setPlayers(GsonPlayer[] players) {
        this.players = players;
    }

    @Override
    public Object convert() {
        return null;
    }
}
