package ar.edu.utn.dds.k3003.repositories;

import java.util.*;

import ar.edu.utn.dds.k3003.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static ar.edu.utn.dds.k3003.model.TipoSuscripcion.ViandasDisponibles;

public class ColaboradorRepository {

    private PesosPuntos pesosPuntos;
    private EntityManagerFactory entityManagerFactory ;
    public ColaboradorRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Colaborador save(Colaborador colaborador) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(colaborador);
        entityManager.getTransaction().commit();
        entityManager.close();
        return colaborador;
    }

    public Colaborador findById(Long id) throws NoSuchElementException{
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Colaborador colaborador= entityManager.find(Colaborador.class, id);
        entityManager.close();
        return colaborador;
    }

    public List<FormasDeColaborarEnum> cambiarFormas(Long id, List<FormasDeColaborarEnum> list) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Colaborador colaborador= entityManager.find(Colaborador.class, id);
        List<FormasDeColaborarEnum> formasDeColaborarViejo =colaborador.getFormas();
        colaborador.setFormas(list);
        entityManager.getTransaction().commit();
        entityManager.close();
        return formasDeColaborarViejo;
    }
    public void suscribirse(SuscripcionDTO suscripcion) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Colaborador colaborador= entityManager.find(Colaborador.class, suscripcion.getId());
        //colaborador.setSuscripcion(suscripcion.getTipoSuscripcion());
        //colaborador.setCantidadFaltante(suscripcion.getCantidadFaltante());
        //colaborador.setCantidadDisponible(suscripcion.getCantidadDisponible());
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void sumarPesos(Long id, Integer cantPesos) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Colaborador colaborador= entityManager.find(Colaborador.class, id);
        colaborador.sumarPesos(cantPesos);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void sumarHeladera(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Colaborador colaborador= entityManager.find(Colaborador.class, id);
        colaborador.sumarheladerasReparadas();
        entityManager.getTransaction().commit();
        entityManager.close();
    }
    public Collection<Colaborador> getColaboradores() {return null;}

    public void borrarRepository() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try{
            em.createQuery("DELETE FROM Colaborador").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE colaborador_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        }catch(RuntimeException e){
            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally{
            em.close();
        }
    }

    public void actualizarPesosPuntos(PesosPuntos pesosPuntos){
        this.pesosPuntos=pesosPuntos;

    }

    public Double getPesosDonados() {
       // PesosPuntos pesosPuntos = entityManager.find(PesosPuntos.class,1);
        return pesosPuntos.getPesosDonados();
    }
    public Double getViandas_Distribuidas() {

      //  PesosPuntos pesosPuntos = entityManager.find(PesosPuntos.class,1);
        return pesosPuntos.getViandas_Distribuidas();
    }
    public Double getViandasDonadas() {

   //     PesosPuntos pesosPuntos = entityManager.find(PesosPuntos.class,1);
        return pesosPuntos.getViandasDonadas();
    }
    public Double getTarjetasRepartidas() {

     //   PesosPuntos pesosPuntos = entityManager.find(PesosPuntos.class,1);
        return pesosPuntos.getTarjetasRepartidas();
    }

    public Double getHeladerasActivas() {
    //    PesosPuntos pesosPuntos = entityManager.find(PesosPuntos.class,1);
        return pesosPuntos.getHeladerasActivas();
    }

}
