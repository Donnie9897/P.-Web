package Practica3.Entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.io.Serializable;

@Entity
public class Comprado implements Serializable {
    @Id
    private int productId;
    @Id
    private long ventaID;
    private int cantidad;
    private int precio;
    private String nombre;

    public Comprado(int productId, long ventaID, int cantidad, int precio, String nombre) {
        this.productId = productId;
        this.ventaID = ventaID;
        this.cantidad = cantidad;
        this.precio = precio;
        this.nombre = nombre;
    }

    public Comprado() {

    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public long getVentaID() {
        return ventaID;
    }

    public void setVentaID(long ventaID) {
        this.ventaID = ventaID;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public int total(){
        return cantidad * precio;
    }
}
