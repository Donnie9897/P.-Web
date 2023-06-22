package Practica3.Services;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServicioDBS <T>{
    private static EntityManagerFactory emf;
    private static ServicioDBS instancia;
    private Class<T> clase;

    private ServicioDBS(){
        regDriver();
    }

    public static ServicioDBS getInstance() {
        if(instancia == null){
            instancia = new ServicioDBS();
        }
        return instancia;
    }

    public ServicioDBS(Class<T> clase){
        if(emf == null){
            emf = Persistence.createEntityManagerFactory("MiUnidadPersistencia");
            this.clase = clase;
        }
    }

    public EntityManager getEntityManager(){
        return emf.createEntityManager();
    }

    public T create(T entity) throws IllegalArgumentException, EntityExistsException, PersistenceException{
        EntityManager em = getEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }finally {
            em.close();
        }
        return entity;
    }

    public T edit(T entity) throws PersistenceException{
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try{
            em.merge(entity);
            em.getTransaction().commit();
        }finally {
            em.close();
        }
        return entity;
    }

    public boolean delete(Object entityID) throws PersistenceException{
        boolean ok = false;
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        try{
            T entity = em.find(clase,entityID);
            em.remove(entity);
            em.getTransaction().commit();

            ok = true;
        }finally {
            em.close();
        }
        return ok;
    }
    public T find(Object id) throws PersistenceException{
        EntityManager em = getEntityManager();

        try {
            return em.find(clase,id);
        }finally {
            em.close();
        }

    }

    public List<T> findAll() throws PersistenceException {
        EntityManager em = getEntityManager();
        try{
            CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(clase);
            criteriaQuery.select(criteriaQuery.from(clase));
            return em.createQuery(criteriaQuery).getResultList();
        } finally {
            em.close();
        }
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://guachiman-9503.7tt.cockroachlabs.cloud:26257/defaultdb", "20180585_ce_pucmm_ed", "aYjBV9TFwnwGUP5TMW-XQw");
        } catch (SQLException exception){
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, exception);
        }
        return connection;
    }

    public void testConnection() {
        try {
            getConnection().close();
            System.out.println("Conexión realizado con exito...");
        } catch (SQLException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void regDriver() {
        try{
            Class.forName("org.postgresql.Driver");
        }catch (ClassNotFoundException exception){
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, exception);

        }
    }

}
