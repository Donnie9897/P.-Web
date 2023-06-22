package org.example;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Sales {
    private long id;
    private String nom;
    private ArrayList<Producto> list_pro;
    Date date;

    public Sales(long id, String nom, ArrayList<Producto> list_pro) {
        this.id = id;
        this.nom = nom;
        this.list_pro = list_pro;
        this.date = new Date();
    }

    public long getId() {
        return id;
    }


    public ArrayList<Producto> getListaProductos() {
        return this.list_pro;
    }
    public String getNombreCliente() {
        return nom;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nom = nombreCliente;
    }



}
