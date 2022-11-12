package com.jyoon.hackathon2022_test3;

public class User {
    public String id;
    public String lat;
    public String lng;
    public String tree_name;
    public String user_name;

    public User () {
        this.id = "";
        this.lat = "";
        this.lng = "";
        this.tree_name = "";
        this.user_name = "";
    }
    public User(String id, String tree_name, String user_name, String lng, String lat) {
        super();
        this.id = id;
        this.tree_name = tree_name;
        this.user_name = user_name;
        this.lng = lng;
        this.lat = lat;
    }
}