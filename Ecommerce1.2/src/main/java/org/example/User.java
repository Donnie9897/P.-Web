package org.example;

public class User {
    private String user;
    private String nom;
    private String pwd;

    public User() {
        this.user = "";
        this.pwd = "";
    }

    public User(String user, String nom, String password) {
        this.user = user;
        this.nom = nom;
        this.pwd = password;
    }

}
