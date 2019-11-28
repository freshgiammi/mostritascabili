package com.example.mostritascabili;

public class MapObject {
    int id;
    double lat;
    double lon;
    String type;
    String size;
    String name;

    public MapObject(int id, double lat, double lon, String type, String size, String name) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        this.size = size;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MapObject{" +
                "id=" + id +
                ", lat=" + lat +
                ", lon=" + lon +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
