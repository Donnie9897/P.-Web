package org.example.clases;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
public class VProducto implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    @Temporal(TemporalType.DATE)
    private Date fechaCompra;
    private String nombreCliente;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Comprado> listaProductos;

    public VProducto() {

    }

    public VProducto(String nombre) {
        this.nombreCliente = nombre;
        fechaCompra = new Date();
    }

    public long getId() {
        return id;
    }

    public String getFechaCompra() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        String date = dateFormat.format(fechaCompra);
        return date;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public List<Comprado> getListaProductos() {
        return listaProductos;
    }

    public Integer getTotal(){
        Integer total = 0;
        for (Comprado producto : listaProductos) {
            total += producto.getPrecio()*producto.getCantidad();
        }
        return total;
    }

    public void setId(long id) {
        this.id = id;
    }


    public void setListaProductos(List<Comprado> listaProductos) {
        this.listaProductos = listaProductos;
    }

}
