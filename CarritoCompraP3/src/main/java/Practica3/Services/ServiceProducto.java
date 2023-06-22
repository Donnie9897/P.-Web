package Practica3.Services;


import Practica3.Entidades.Producto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceProducto extends ServicioDBS<Producto>{
    private static ServiceProducto instancia;

    private ServiceProducto(){
        super(Producto.class);
    }

    public static ServiceProducto getInstancia(){
        if(instancia == null){
            instancia = new ServiceProducto();
        }
        return instancia;
    }

    public void deleteProducto(Object id){
        Producto entity = find(id);
        entity.setEstado(false);
        entity = edit(entity);
    }


    public List<Producto> findProd(int ini, int fin) throws PersistenceException {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from PRODUCTO WHERE ESTADO = true ", Producto.class);
        query.setFirstResult(ini);
        if(fin != 0) {
            query.setMaxResults(fin);
        }
        List<Producto> lista = (List<Producto>) query.getResultList().stream().limit(10).collect(Collectors.toList());;
        /*for (int i = 0; i < lista.size(); i++){
            System.out.println(lista.get(i).getId());
        }*/
        return lista;
    }

    public int pag() {
        int pageSize = 10;
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from PRODUCTO WHERE ESTADO = true ", Producto.class);
        int countResults = query.getResultList().size();
        int lastPageNumber = (int) (Math.ceil(countResults / pageSize));
        return  lastPageNumber;
    }
}
