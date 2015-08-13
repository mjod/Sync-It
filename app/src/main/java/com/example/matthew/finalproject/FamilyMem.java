package com.example.matthew.finalproject;

/**
 * Created by Matthew on 4/21/2015.
 */
public class FamilyMem {
    String username;
    String name;
    int parent;

    public FamilyMem(String username, String name, int parent) {
        this.username = username;
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public int getParent() {
        return parent;
    }

    public String getUsername() {
        return username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
