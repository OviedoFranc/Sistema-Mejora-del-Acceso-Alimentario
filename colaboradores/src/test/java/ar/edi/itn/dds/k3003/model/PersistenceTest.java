package ar.edi.itn.dds.k3003.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.model.FormasDeColaborarEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ar.edu.utn.dds.k3003.model.FormasDeColaborarEnum.DONADOR;
import static ar.edu.utn.dds.k3003.model.FormasDeColaborarEnum.TRANSPORTADOR;
import static ar.edu.utn.dds.k3003.model.FormasDeColaborarEnum.TECNICO;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersistenceTest {

    static EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    @BeforeAll
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("copiamedb");
    }

    @BeforeEach
    public void setup() throws Exception {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    public void testConectar() {
        // vacío, para ver que levante el ORM
    }

    @Test
    public void testGuardarYRecuperarDoc() throws Exception {
        Colaborador col1 = new Colaborador(null,"FUNCIONA LETS GO", List.of(FormasDeColaborarEnum.DONADOR, TRANSPORTADOR));
        Colaborador col3 = new Colaborador(null,"FUNCIONA CON 2?", List.of(FormasDeColaborarEnum.DONADOR));
        entityManager.getTransaction().begin();
        entityManager.persist(col1);
        entityManager.persist(col3);
        entityManager.getTransaction().commit();
        entityManager.close();

        entityManager = entityManagerFactory.createEntityManager();
        Colaborador col2 = entityManager.find(Colaborador.class,1L);
        Colaborador col4 = entityManager.find(Colaborador.class,2L);

        assertEquals(col1.getNombre(), col2.getNombre()); // también puede redefinir el equals
       // assertEquals(col2.getFormas(),List.of(FormaDeColaborarEnum.DONADOR, TRANSPORTADOR)); Cuidado con el tipo de lista
        assertEquals(col4.getNombre(), col3.getNombre()); // también puede redefinir el equals
    }

}
