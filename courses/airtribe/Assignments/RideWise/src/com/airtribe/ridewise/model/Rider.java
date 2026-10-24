package com.airtribe.ridewise.model;

public class Rider {

    private String id;
    private String name;
    private int location;   // simple km point on the city map, e.g. 5

    public Rider(String id, String name, int location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Rider[id=" + id + ", name=" + name + ", location=" + location + "]";
    }
}
