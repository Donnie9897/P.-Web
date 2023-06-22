package Practica3.Entidades;

import java.util.ArrayList;

public class Carrito {
    private long id;
    public ArrayList<Producto> productos;

    public Carrito(long id) {
        this.id = id;
        this.productos = new ArrayList<Producto>();
    }

    public Carrito() {
        this.productos = new ArrayList<Producto>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public void agregarProducto(Producto nuevo){
        this.productos.add(nuevo);
    }

    public Producto getProductosID(int id) {
        return productos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public void cambiarProducto(Producto temp, int pos) {
        productos.set(pos, temp);
    }

    public int posicion(Integer id) {
        int cont = 0;
        while(cont < productos.size()){
            if(productos.get(cont).getId() == id){
                return cont;
            }
            cont++;
        }
        return -1;
    }

    public void eliminarProductoId(int id) {
        int pos = posicion(id);

        productos.remove(pos);
    }

    public void eliminarProductos() {this.productos = new ArrayList<Producto>();
    }
}
