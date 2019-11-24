package com.example.mostritascabili;

import android.util.Base64;

public class Profile {
    String username;
    String img;
    int xp;
    int lp;

    public Profile(String username, String img, int xp, int lp) {
        this.username = username;
        this.img = img;
        this.xp = xp;
        this.lp = lp;
    }

    public String getUsername() {
        return username;
    }

    public String getImg() {
        return img;
    }

    public int getXp() {
        return xp;
    }

    public int getLp() {
        return lp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setLp(int lp) {
        this.lp = lp;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "username='" + username + '\'' +
                ", img=" + img +
                ", xp=" + xp +
                ", lp=" + lp +
                '}';
    }
}

