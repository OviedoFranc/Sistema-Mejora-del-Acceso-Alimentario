package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.model.Vianda;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.exception.ConstraintViolationException;

@Getter
@Setter
public class ViandaRepository {
  private final EntityManager entityManager;

  public ViandaRepository(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public Vianda save(Vianda vianda) {
    EntityTransaction transaction = entityManager.getTransaction();
    try {
      transaction.begin();
      if (vianda.getId() == null) {
        entityManager.persist(vianda);
      } else {
        entityManager.merge(vianda);
      }
      transaction.commit();
    } catch (PersistenceException pe) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      if (pe.getCause() instanceof ConstraintViolationException) {
        throw new RuntimeException("Ya existe una vianda con el mismo código QR", null);
      }
      throw pe;
    }
    return vianda;
  }

  public Vianda buscarXQR(String qr) {
    TypedQuery<Vianda> query = entityManager.createQuery("SELECT v FROM Vianda v WHERE v.qr = :qr", Vianda.class);
    query.setParameter("qr", qr);
    List<Vianda> resultados = query.getResultList();
    return resultados.isEmpty() ? null : resultados.get(0);
  }

  public List<Vianda> obtenerXColIDAndAnioAndMes(Long colaboradorId, Integer mes, Integer anio) {
    YearMonth yearMonth = YearMonth.of(anio, mes);
    LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
    LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

    TypedQuery<Vianda> query = entityManager.createQuery(
        "SELECT v FROM Vianda v WHERE v.colaboradorId = :colaboradorId "
            + "AND v.fechaElaboracion >= :startOfMonth AND v.fechaElaboracion <= :endOfMonth",Vianda.class);
    query.setParameter("colaboradorId", colaboradorId);
    query.setParameter("startOfMonth", startOfMonth);
    query.setParameter("endOfMonth", endOfMonth);

    return query.getResultList();
  }
  
  public Vianda modificarEstado(String qr, EstadoViandaEnum estado) {
      entityManager.getTransaction().begin();
      Vianda vianda = this.buscarXQR(qr);
      vianda.setEstado(estado);
      this.save(vianda);
      entityManager.merge(vianda);
      entityManager.getTransaction().commit();
      return vianda;
  }

  public void clearDB() {
    entityManager.getTransaction().begin();
    try {
      entityManager.createQuery("DELETE FROM Vianda").executeUpdate();
      entityManager.createNativeQuery("ALTER SEQUENCE viandas_id_seq RESTART WITH 1").executeUpdate();
      entityManager.getTransaction().commit();
    } catch (Exception e) {
      entityManager.getTransaction().rollback();
      throw e;
    }
  }
}