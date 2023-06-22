package org.example.servicios;

import org.example.clases.Coment;
import org.hibernate.*;
import org.example.DBService;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.List;

public class ServiceComentario extends DBService<Coment> {
    private static ServiceComentario instance;

    private ServiceComentario(){
        super(Coment.class);
    }

    public static ServiceComentario getInstancia(){
        if(instance==null){
            instance = new ServiceComentario();
        }
        return instance;
    }

    public List<Coment> findComments(int id) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select e from Coment e where e.estado = true and e.productoId like :id");
        query.setParameter("id", id);
        List<Coment> lista = query.getResultList();
        return lista;
    }
    public void deleteComent(int id) throws PersistenceException {
        Session session = getEntityManager().unwrap(org.hibernate.Session.class);;
        session.beginTransaction();
        Query query = session.createQuery("delete from Coment where id =" + id);
        int row = query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
