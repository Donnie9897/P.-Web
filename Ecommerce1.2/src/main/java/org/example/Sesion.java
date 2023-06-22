package org.example;

import java.util.ArrayList;
import java.util.List;

public class Sesion {
    private static Sesion instancia;
    private long cart;
    private List<User> list_user;
    private List<Producto> list_pro;
    private List<Sales> sales;
    private int i;


    public Sesion() {
        list_user = new ArrayList<>();
        list_pro = new ArrayList<>();
        sales = new ArrayList<>();
        i = 0;
        cart = 0;

        list_user.add(new User("admin","admin","admin"));
    }

    public static Sesion getInstance(){
        if(instancia == null){
            instancia = new Sesion();
        }
        return instancia;
    }


    public List<Producto> getProductos() {
        return list_pro;
    }

    public List<Sales> getSales() {
        return sales;
    }

    public Producto registrarProducto(Producto producto){
        producto.setId(i++);
        list_pro.add(producto);
        return producto;
    }

    public long getCarrito(){
        return cart++;
    }
    public Producto actualizarProducto(Producto producto){
        Producto aux = obtenerProID(producto.getId());
        if(aux == null){
            throw new RuntimeException("No Existe el estudiante: " + producto.getId());
        }
        aux.update(producto);
        return aux;
    }

    public boolean eliminarProducto(int id){
        Producto temp = obtenerProID(id);
        return list_pro.remove(temp);
    }
    public Producto obtenerProID(int id) {
        return list_pro.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public void agregarSale(Sales aux_sale) {
        sales.add(aux_sale);
    }
}

