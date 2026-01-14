package com.handballmanager;

public class TeamModel {

    private int id;
    private String name;

    public TeamModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public TeamModel(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // tosTring for at vise navn på hold
    @Override
    public String toString() {
        return name;
    }
}
