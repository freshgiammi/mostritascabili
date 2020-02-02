package com.example.mostritascabili;

public class HistoryMob {

    int object_id;
    int times;
    String img;

    public HistoryMob(int object_id, int times) {
        this.object_id = object_id;
        this.times = times;
        this.img = null;
    }

    public int getObject_id() {
        return object_id;
    }

    public void setObject_id(int object_id) {
        this.object_id = object_id;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "HistoryMob{" +
                "object_id=" + object_id +
                ", times=" + times +
                ", img='" + img + '\'' +
                '}';
    }
}
