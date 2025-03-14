package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Metrica;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


public class MetricaRepository {

    private static AtomicLong seqId = new AtomicLong();
    private Collection<Metrica> metrica;
    private EntityManager entityManager ;

    public MetricaRepository(EntityManager entityManager){
        this.metrica = new ArrayList<>();
        this.entityManager = entityManager;
    }

    public Metrica save(Metrica metrica) {
        if (Objects.isNull(metrica.getId())) {
            entityManager.getTransaction().begin();
            entityManager.persist(metrica);
            entityManager.getTransaction().commit();
        }
        return metrica;
    }

    public Metrica findById(Long id) {
        Metrica metrica = entityManager.find(Metrica.class, id);
        if (metrica == null) {
            throw new NoSuchElementException(String.format("No existe una vianda con el id: %s", id));
        }
        return metrica;
    }

    public Metrica findByQR(String nombre) {
        String jpql = "SELECT m FROM Metrica m WHERE m.nombre = :nombre";
        TypedQuery<Metrica> query = entityManager.createQuery(jpql, Metrica.class);
        query.setParameter("nombre", nombre);
        Metrica metrica = query.getResultStream().findFirst().orElse(null);
        
        if (metrica == null) {
            throw new NoSuchElementException(String.format("No existe una metrica con el siguiente nombre: %s", nombre));
        }
        return metrica;
    }

    public void aumentarMetrica(String nombre) {
        entityManager.getTransaction().begin();
        Metrica metrica = findByQR(nombre);
        metrica.setValor(metrica.getValor() + 1);
        entityManager.getTransaction().commit();
    }

    public void disminuirMetrica(String nombre) {
        entityManager.getTransaction().begin();
        Metrica metrica = findByQR(nombre);
        metrica.setValor(metrica.getValor() - 1);
        entityManager.getTransaction().commit();
    }

    public void borrarMetricas() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM Metrica").executeUpdate();
        entityManager.getTransaction().commit();
    }
}