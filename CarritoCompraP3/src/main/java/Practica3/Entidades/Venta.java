package Practica3.Entidades;

import jakarta.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Venta implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    @Temporal(TemporalType.DATE)
    private String nombreCliente;
    private Date fecha;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Comprado> listaProductos;

    public Venta(long id, String nombreCliente, ArrayList<Comprado> listaProductos) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.listaProductos = listaProductos;
        this.fecha = new Date();
    }

    public Venta() {
    }

    public Venta(String nombre) {
        this.nombreCliente = nombre;
        fecha = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public List<Comprado> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Comprado> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public String getFecha() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        String temp = dateFormat.format(fecha);
        return temp;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getTotal(){
        Integer total = 0;
        for (Comprado producto : listaProductos) {
            total += producto.getPrecio()*producto.getCantidad();
        }
        return total;
    }
}
