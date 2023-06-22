package org.example;

import java.util.ArrayList;

public class Cart {
    private long id;
    public ArrayList<Producto> aux_pro;
    public Cart(long id) {

        this.id = id;
        this.aux_pro = new ArrayList();
    }

    public ArrayList<Producto> getProductos() {
        return this.aux_pro;
    }

    public void agregar(Producto aux) {
        this.aux_pro.add(aux);
    }


    public Producto getProID(int id) {

        return aux_pro.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }


    public void editPro(Producto aux, int i) {
        this.aux_pro.set(i, aux);
    }

    public int position(Integer id) {
        int i = 0;

        while(i < aux_pro.size()){
            if(aux_pro.get(i).getId() == id){
                return i;
            }
            i++;
        }
        return -1;
    }

    public void eliminarProId(int id) {
        int pos = this.position(id);
        this.aux_pro.remove(pos);
    }

    public void eliminarPro() {
        this.aux_pro = new ArrayList();
    }

}
