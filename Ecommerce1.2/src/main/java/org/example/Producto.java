package org.example;

public class Producto {
    private int id;
    private String nom;
    private int cant;
    private int aux_prc;


    public Producto( String nom, int aux_prc) {
        this.nom = nom;
        this.aux_prc = aux_prc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getPrecio() {
        return aux_prc;
    }

    public int getCantidad() {
        return cant;
    }

    public void setCantidad(int cant) {
        this.cant = cant;
    }
    public void update(Producto producto) {
        this.nom = producto.nom;
        this.aux_prc = producto.aux_prc;
    }

}
