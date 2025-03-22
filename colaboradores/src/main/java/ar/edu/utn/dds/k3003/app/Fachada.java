package ar.edu.utn.dds.k3003.app;

import java.util.List;
import java.util.NoSuchElementException;
import ar.edu.utn.dds.k3003.clients.FachadaHeladera;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.model.exceptions.ErrorConParametrosException;
import ar.edu.utn.dds.k3003.repositories.ColaboradorMapper;
import ar.edu.utn.dds.k3003.repositories.ColaboradorRepository;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.EntityManagerFactory;

@Slf4j
public class Fachada  {

    public final ColaboradorRepository colaboradorRepository;
    private final ColaboradorMapper colaboradorMapper;
    private FachadaLogistica logisticaFachada;
    private FachadaViandas viandasFachada;
    private FachadaHeladera heladerasFachada;

    public Fachada(EntityManagerFactory entityFactory) {
        this.colaboradorRepository =new ColaboradorRepository(entityFactory);
        this.colaboradorMapper=new ColaboradorMapper();
    }

    public ColaboradorDTO agregar(ColaboradorDTO colaboradorDTO) {

        Colaborador colaborador = new Colaborador(colaboradorDTO.getId(),colaboradorDTO.getNombre(), colaboradorDTO.getFormas()
        );
        colaborador = this.colaboradorRepository.save(colaborador);
        return colaboradorMapper.map(colaborador);
    }

    public void suscribirse(SuscripcionDTO2 suscripcion) {
            this.heladerasFachada.postSubscripcion(suscripcion.getHeladeraId(),suscripcion);
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
}

