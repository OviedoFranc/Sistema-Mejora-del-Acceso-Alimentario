package ar.edu.utn.dds.k3003.app;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import ar.edu.utn.dds.k3003.clients.FachadaHeladera;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.model.exceptions.ErrorConParametrosException;
import ar.edu.utn.dds.k3003.repositories.ColaboradorMapper;
import ar.edu.utn.dds.k3003.repositories.ColaboradorRepository;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static ar.edu.utn.dds.k3003.model.TipoSuscripcion.ViandasDisponibles;


@Slf4j
public class Fachada  { //ya no implementa la FachadaColaboradores del profe xq esta en read-only

    public final ColaboradorRepository colaboradorRepository;
    private final ColaboradorMapper colaboradorMapper;
    private FachadaLogistica logisticaFachada;
    private FachadaViandas viandasFachada;

    private FachadaHeladera heladerasFachada;
    final LocalDateTime now = LocalDateTime.now();
    public static EntityManagerFactory entityManagerFactory;


    public  Fachada() {     //pónerle void?
        startEntityManagerFactory();
        EntityManager entityManager= entityManagerFactory.createEntityManager();
        this.colaboradorRepository =new ColaboradorRepository(entityManagerFactory);
        this.colaboradorMapper=new ColaboradorMapper();
    }


    public ColaboradorDTO agregar(ColaboradorDTO colaboradorDTO) {

        Colaborador colaborador = new Colaborador(colaboradorDTO.getId(),colaboradorDTO.getNombre(), colaboradorDTO.getFormas()
        );
        colaborador = this.colaboradorRepository.save(colaborador);
        return colaboradorMapper.map(colaborador);
    }

    public void suscribirse(SuscripcionDTO2 suscripcion) {

    //    SuscripcionDTO2 s2 = new SuscripcionDTO2(suscripcion.getId(), suscripcion.getHeladeraId(), suscripcion.getTipoSuscripcion().get(0), suscripcion.getCantidadDisponible());
   //     for(int i=0;i<suscripcion.getTipoSuscripcion().size();i++)
    //    {
    //        if(suscripcion.getTipoSuscripcion().get(i) == ViandasDisponibles)
   //             s2.setCantidadN(suscripcion.getCantidadDisponible());
    //        else
  //              s2.setCantidadN(suscripcion.getCantidadFaltante());

            this.heladerasFachada.postSubscripcion(suscripcion.getHeladeraId(),suscripcion);
   //     }
        //  this.colaboradorRepository.suscribirse(suscripcion);
    }

    public void actualizarPesosPuntos(Double pesosDonados,Double viandas_Distribuidas, Double viandasDonadas,
                                      Double tarjetasRepartidas, Double heladerasActivas,Double heladerasReparadas) throws ErrorConParametrosException {
    this.colaboradorRepository.actualizarPesosPuntos(new PesosPuntos(pesosDonados,viandas_Distribuidas,viandasDonadas,tarjetasRepartidas,heladerasActivas,heladerasReparadas));
    }


    public Double puntos(Long colaboradorId) throws NoSuchElementException{
        Double puntos = 0.0;
        Colaborador colaborador = colaboradorRepository.findById(colaboradorId);

            Double pesoPesosDonados=colaboradorRepository.getPesosDonados();
            puntos+=pesoPesosDonados*colaborador.getPesosDonados();

            Double pesoHeladerasReparadas=colaboradorRepository.getHeladerasActivas();
            puntos+=pesoHeladerasReparadas*colaborador.getHeladerasReparadas();

            Double pesoViandasDon=colaboradorRepository.getViandasDonadas();
            puntos+= pesoViandasDon * this.viandasFachada.viandasDeColaborador(colaboradorId, 5, 2024).size();

            Double pesoViandasDist=colaboradorRepository.getViandas_Distribuidas();
            puntos+= pesoViandasDist * this.logisticaFachada.trasladosDeColaborador(colaboradorId, 5 , 2024).size();

        return puntos;
    }

    public List<FormasDeColaborarEnum> obtenerFormas(Long colaboradorId) {
        return colaboradorRepository.findById(colaboradorId).getFormas();
    }

    public List<FormasDeColaborarEnum> modificar(Long id, List<FormasDeColaborarEnum> list) throws NoSuchElementException {
        List<FormasDeColaborarEnum> formasDeColaborarViejo = colaboradorRepository.cambiarFormas(id,list);
        return formasDeColaborarViejo;
    }


    public Colaborador  buscarXId(Long colaboradorId) throws NoSuchElementException {
        return colaboradorRepository.findById(colaboradorId);
    }


    public void reportarHeladera(Integer heladeraId) {
        this.heladerasFachada.getReportarFalla(heladeraId);
    }

    public void repararHeladera(Long id, Integer heladeraId) {
        try {
            if (this.obtenerFormas(id).contains(FormasDeColaborarEnum.TECNICO)) {
                colaboradorRepository.sumarHeladera(id);
                this.heladerasFachada.getArreglarFalla(heladeraId);
            } else {
                throw new IllegalArgumentException("No se puede crear la ruta porque el colaborador no es un tecnico");
            }
        }catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la ruta: " + e.getMessage(), e);
        }
    }
    public void sumarPesos(Long id, Integer cantPesos) {
        colaboradorRepository.sumarPesos(id,cantPesos);
    }



    public void avisar(SuscripcionDTO2 suscripcion) {
        Long id = suscripcion.getId();
        Integer cantidadN= suscripcion.getCantidadN();
        Integer heladeraId= suscripcion.getHeladeraId();
        switch (suscripcion.getTipoSuscripcion()){
            case FaltanteViandas -> log.warn("Colaborador {}, en la heladera {} sólo quedan {} lugares, llevelas a otra heladera!!! ",id,heladeraId,cantidadN);
            case ViandasDisponibles -> log.warn("Colaborador {}, en la heladera {} sólo quedan {} viandas, se requiere que la restockee!!! ",id,heladeraId,cantidadN);
            case HeladeraDesperfecto -> log.warn("Colaborador {}, la heladera {} sufrió un desperfecto,sus viandas requieren ser movidas!!! ",id,heladeraId);
        }
    }

    public void setLogisticaProxy(FachadaLogistica logistica) {this.logisticaFachada=logistica;}


    public void setViandasProxy(FachadaViandas viandas) {this.viandasFachada=viandas;}

    public void setHeladerasProxy(FachadaHeladera heladeras) {this.heladerasFachada=heladeras;}


    public void borrarDB() {
        colaboradorRepository.borrarRepository();
    }
    public static void startEntityManagerFactory() {
// https://stackoverflow.com/questions/8836834/read-environment-variables-in-persistence-xml-file
        Map<String, String> env = System.getenv();
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        String[] keys = new String[] { "javax.persistence.jdbc.url", "javax.persistence.jdbc.user",
                "javax.persistence.jdbc.password", "javax.persistence.jdbc.driver", "hibernate.hbm2ddl.auto",
                "hibernate.connection.pool_size", "hibernate.show_sql" };
        for (String key : keys) {
            if (env.containsKey(key)) {
                String value = env.get(key);
                configOverrides.put(key, value);
            }
        }
        entityManagerFactory = Persistence.createEntityManagerFactory("db", configOverrides);
    }

}

