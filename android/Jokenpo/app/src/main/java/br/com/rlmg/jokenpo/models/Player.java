package br.com.rlmg.jokenpo.models;

import java.util.Date;

/**
 * Created by rlmg on 4/2/17.
 */

public class Player {

    private String id;
    private String name;
    private String firebaseToken;
    private boolean logged;
    private int version;
    private Date createdAt;


    public Player() {
    }

    public Player(String id, String name, String firebaseToken, boolean logged, int version, Date createdAt) {
        this.id = id;
        this.name = name;
        this.firebaseToken = firebaseToken;
        this.logged = logged;
        this.version = version;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
