package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.Exception.TemperaturasNoEncontradasException;
import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.ViandaMapper;
import ar.edu.utn.dds.k3003.repositories.ViandaRepository;
import ar.edu.utn.dds.k3003.service.UtilsMetrics;

import java.util.List;
import java.util.NoSuchElementException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class Fachada implements FachadaViandas {
	private final ViandaMapper viandaMapper;
	  private final ViandaRepository viandaRepository;
	  private FachadaHeladeras fachadaHeladeras;

    public Fachada(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
	    this.viandaMapper = new ViandaMapper();
	    this.viandaRepository = new ViandaRepository(entityManager);
	  }

  @Override
  public ViandaDTO agregar(ViandaDTO viandaDTO) {
    Vianda vianda = new Vianda(viandaDTO.getCodigoQR(),
            viandaDTO.getColaboradorId(),
            viandaDTO.getHeladeraId(),
            EstadoViandaEnum.PREPARADA,
            viandaDTO.getFechaElaboracion());
    this.viandaRepository.save(vianda);
    UtilsMetrics.actualizarViandasCreadas();
    return viandaDTO;
  }
  
  public ViandaDTO agregarYDepositar(ViandaDTO viandaDTO) {
	  ViandaDTO viandaADepositar = agregar(viandaDTO);
	  fachadaHeladeras.depositar(viandaADepositar.getHeladeraId(), viandaADepositar.getCodigoQR());
	  
	  return viandaADepositar;
  }

  @Override
  public ViandaDTO modificarEstado(String qr, EstadoViandaEnum estadoViandaEnum)
      throws NoSuchElementException {
    Vianda viandaEncontrada = viandaRepository.buscarXQR(qr);
    EstadoViandaEnum estadoActual = viandaEncontrada.getEstado();
    if(estadoActual == EstadoViandaEnum.PREPARADA && estadoViandaEnum == EstadoViandaEnum.EN_TRASLADO) {
    	UtilsMetrics.actualizarViandasEnTransporte(true);
    }
    if(estadoActual == EstadoViandaEnum.EN_TRASLADO && estadoViandaEnum == EstadoViandaEnum.DEPOSITADA) {
    	UtilsMetrics.actualizarViandasEnTransporte(false);
    }
    if(estadoViandaEnum == EstadoViandaEnum.VENCIDA) {
    	UtilsMetrics.actualizarViandasVencidas();
    }
    viandaEncontrada.setEstado(estadoViandaEnum);
    viandaEncontrada = viandaRepository.save(viandaEncontrada);
    return viandaMapper.map(viandaEncontrada);
  }

  @Override
  public List<ViandaDTO> viandasDeColaborador(Long colaboradorId, Integer mes, Integer anio)
      throws NoSuchElementException {
    return viandaRepository.obtenerXColIDAndAnioAndMes(colaboradorId, mes, anio).stream()
        .map(viandaMapper::map)
        .toList();
  }

  @Override
  public ViandaDTO buscarXQR(String qr) throws NoSuchElementException {
    Vianda viandaEncontrada = viandaRepository.buscarXQR(qr);
    if (viandaEncontrada == null) {
      throw new NoSuchElementException("Vianda no encontrada");
    }
    return viandaMapper.map(viandaEncontrada);
  }

  @Override
  public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {
    this.fachadaHeladeras = fachadaHeladeras;
  }

  @Override
  public boolean evaluarVencimiento(String qr) throws NoSuchElementException {
    Vianda viandaEncontrada = viandaRepository.buscarXQR(qr);
    List<TemperaturaDTO> temperaturas = fachadaHeladeras.obtenerTemperaturas(viandaEncontrada.getHeladeraId());
    if (temperaturas.isEmpty()) {
      throw new TemperaturasNoEncontradasException("No se encontraron temperaturas para la heladera con ID: " + viandaEncontrada.getHeladeraId());
    }
    return temperaturas.stream()
        .anyMatch(temperaturaDTO -> temperaturaDTO.getTemperatura() >= 5);
  }

  @Override
  public ViandaDTO modificarHeladera(String qr, int nuevaHeladera) {
    Vianda viandaEncontrada = viandaRepository.buscarXQR(qr);
    viandaEncontrada.setHeladeraId(nuevaHeladera);
    viandaEncontrada = viandaRepository.save(viandaEncontrada);
    return viandaMapper.map(viandaEncontrada);
  }

  public void clearDB(){
    viandaRepository.clearDB();
  }

}
