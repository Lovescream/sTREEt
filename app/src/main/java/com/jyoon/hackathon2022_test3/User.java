package com.jyoon.hackathon2022_test3;

public class User {
    String id;
    String name;
    String password;

    public User () {
        this.id = "";
        this.name = "";
        this.password = "";
    }
    public User(String name, String password) {
        super();
        this.id = "";
        this.name = name;
        this.password = password;
    }
}