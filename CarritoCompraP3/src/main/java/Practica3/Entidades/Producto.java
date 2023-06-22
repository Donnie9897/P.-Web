package Practica3.Entidades;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Producto implements Serializable{
    @Id
    @GeneratedValue
    private int id;
    private  String nombre;
    private int precio;
    @Transient
    private int cantidad;

    private String descripcion;
    @Column(columnDefinition = "boolean default true")
    private boolean estado;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Foto> fotos;

    public Producto(String nombre, int precio, String descripcion) {
        this.nombre = nombre;
        this.precio = precio;
        estado = true;
        this.descripcion = descripcion;
    }

    public Producto() {

    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isEstado() {
        return estado;
    }

    public List<Foto> getFotos() {
        return fotos;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public void setFotos(List<Foto> fotos) {
        this.fotos = fotos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void actualizar(Producto producto) {
        this.nombre = producto.nombre;
        this.precio = producto.precio;
    }

    public int total(){
        return precio * cantidad;
    }

}
