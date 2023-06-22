package org.example.servicios;

import org.example.DBService;
import org.example.clases.Comprado;
import org.example.clases.Producto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class ServiceProdComprado extends DBService<Comprado> {
    private static ServiceProdComprado instance;

    private ServiceProdComprado(){ super(Comprado.class);}

    public static ServiceProdComprado getInstance(){
        if(instance == null){
            instance = new ServiceProdComprado();
        }
        return instance;
    }

    public List<Comprado> convertProd(List<Producto> productos, long venta){
        List<Comprado> list = new ArrayList<Comprado>();
        for (Producto prod:productos) {
            Comprado temp = new Comprado(prod.getId(),venta,prod.getCantidad(),prod.getPrecio(),prod.getNombre());
            getInstance().create(temp);
            list.add(temp);
        }
        return list;
    }

    public List<Comprado> getProd() {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from PRODCOMPRADO ", Comprado.class);
        List<Comprado> lista = query.getResultList();
        return lista;
    }
}
