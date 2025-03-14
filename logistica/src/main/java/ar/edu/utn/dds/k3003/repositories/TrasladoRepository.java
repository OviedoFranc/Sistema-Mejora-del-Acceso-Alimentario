package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.model.Ruta;
import ar.edu.utn.dds.k3003.model.Traslado;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TrasladoRepository {
    private static AtomicLong seqId = new AtomicLong();
    private Collection<Traslado> traslados;
    private EntityManager entityManager ;

    public TrasladoRepository(EntityManager entityManager){

        this.traslados = new ArrayList<>();
        this.entityManager = entityManager;
    }

    public Traslado save(Traslado traslado) {
        if (Objects.isNull(traslado.getId())) {
            entityManager.getTransaction().begin();
            entityManager.persist(traslado);
            entityManager.getTransaction().commit();
        }
        return traslado;
    }

    public Traslado findById(Long id) {

        Traslado traslado = entityManager.find(Traslado.class, id);
        if (traslado == null) {
            throw new NoSuchElementException(String.format("No hay una ruta de id: %s", id));
        }
        return traslado;
    }

    public List<Traslado> findByColaboradorId(Long id, Integer mes, Integer anio) {
/*
        List<Traslado> trasladosDelColaborador = this.traslados.stream()
                .filter(t -> t.getRuta().getColaboradorId().equals(id))
                .filter(x -> x.getFechaTraslado().getMonthValue() == mes)
                .filter(x -> x.getFechaTraslado().getYear() == anio)
                .collect(Collectors.toList());

 */

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Traslado> criteriaQuery = criteriaBuilder.createQuery(Traslado.class);
        Root<Traslado> root = criteriaQuery.from(Traslado.class);
        criteriaQuery.select(root)
                .where(
                        criteriaBuilder.equal(root.get("ruta").get("colaboradorId"), id)
                );


        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public Traslado modificarEstado(Long id, EstadoTrasladoEnum estadoNuevo) {
        entityManager.getTransaction().begin();
        Traslado traslado = findById(id);
        traslado.setEstado(estadoNuevo);
        entityManager.getTransaction().commit();

        return traslado;
    }

    public Long maximoId(){

        Long maxTrasladoId = (Long) entityManager.createQuery("SELECT MAX(t.id) FROM Traslado t")
                .getSingleResult();

        return maxTrasladoId;
    }

    public void borrarTraslado_Viandas(Long trasladoId){
        entityManager.getTransaction().begin();
        String jpql = "DELETE FROM traslado_viandas t WHERE t.traslado_id = :trasladoId";
        entityManager.createQuery(jpql)
                .setParameter("trasladoId", trasladoId)
                .executeUpdate();
        entityManager.getTransaction().commit();
    }
    public void borrarTraslados() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM traslados").executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();
    }



}
