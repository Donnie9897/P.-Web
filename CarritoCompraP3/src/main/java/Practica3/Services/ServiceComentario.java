package Practica3.Services;

import Practica3.Entidades.Comentarios;
import org.hibernate.*;



import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import java.util.List;
public class ServiceComentario extends ServicioDBS<Comentarios> {
    private static ServiceComentario instancia;

    private ServiceComentario(){
        super(Comentarios.class);
    }

    public static ServiceComentario getInstancia(){
        if(instancia ==null){
            instancia = new ServiceComentario();
        }
        return instancia;
    }

    public List<Comentarios> findComments(int id) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select e from Comentarios e where e.estado = true and e.productoId =: id");
        query.setParameter("id", id);
        List<Comentarios> lista = query.getResultList();
        return lista;
    }
    public void deleteComent(int id) throws PersistenceException {
        Session session = getEntityManager().unwrap(Session.class);
        session.beginTransaction();
        Query query = session.createQuery("delete from Comentarios where id =" + id);
        int row = query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
