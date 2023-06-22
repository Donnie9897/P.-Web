package org.example.servicios;

import org.example.DBService;
import org.example.clases.VProducto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class ServicioVentas extends DBService<VProducto> {

    private static ServicioVentas instance;

    private ServicioVentas(){ super(VProducto.class);}

    public static ServicioVentas getInstance(){
        if(instance == null){
            instance = new ServicioVentas();
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
