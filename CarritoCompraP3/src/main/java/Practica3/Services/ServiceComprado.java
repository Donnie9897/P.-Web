package Practica3.Services;

import Practica3.Entidades.Comprado;
import Practica3.Entidades.Producto;

import java.util.ArrayList;
import java.util.List;

public class ServiceComprado extends ServicioDBS<Comprado>{

    private static ServiceComprado instancia;

    private ServiceComprado(){ super(Comprado.class);}

    public static ServiceComprado getInstancia(){
        if(instancia == null){
            instancia = new ServiceComprado();
        }
        return instancia;
    }

    public List<Comprado> convertProd(List<Producto> productos, long venta){
        List<Comprado> list = new ArrayList<Comprado>();
        for (Producto prod:productos) {
            Comprado temp = new Comprado(prod.getId(),venta,prod.getCantidad(),prod.getPrecio(),prod.getNombre());
            getInstancia().create(temp);
            list.add(temp);
        }
        return list;
    }

}
