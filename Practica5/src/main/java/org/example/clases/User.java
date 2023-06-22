package org.example.clases;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class User implements Serializable {
    @Id
    private String usuario;
    private String password;

    public User() {
        usuario ="";
        password = "";
    }
    public User(String user, String pass) {
        usuario = user;
        password = pass;
    }

    public String getUsuario() {
        return usuario;
    }
    public String getPassword() {
        return password;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
