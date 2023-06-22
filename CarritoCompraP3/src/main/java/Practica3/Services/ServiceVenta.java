package Practica3.Services;

import Practica3.Entidades.Venta;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

public class ServiceVenta extends ServicioDBS<Venta>{
    private static ServiceVenta instancia;

    private ServiceVenta(){ super(Venta.class);}

    public static ServiceVenta getInstancia(){
        if(instancia == null){
            instancia = new ServiceVenta();
        }
        return instancia;
    }

    public List<Venta> getVentas(){
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from VENTA ", Venta.class);
        List<Venta> lista = query.getResultList();
        return lista;
    }
}
