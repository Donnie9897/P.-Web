package org.example.servicios;

import org.example.DBService;
import org.example.clases.VProducto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class ServiceVentas extends DBService<VProducto> {

    private static ServiceVentas instance;

    private ServiceVentas(){ super(VProducto.class);}

    public static ServiceVentas getInstance(){
        if(instance == null){
            instance = new ServiceVentas();
        }
        return instance;
    }

    public List<VProducto> getVentas(){
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from VENTASPRODUCTOS ", VProducto.class);
        List<VProducto> lista = query.getResultList();
        return lista;
    }
}
