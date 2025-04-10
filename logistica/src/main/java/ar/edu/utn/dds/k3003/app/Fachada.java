package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.Service.FachadaColaboradoresModificada;
import ar.edu.utn.dds.k3003.Service.FormasDeColaborarEnum;
import ar.edu.utn.dds.k3003.facades.FachadaColaboradores;
import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.Utils.TrasladoDTO;
import ar.edu.utn.dds.k3003.exceptions.TrasladoNoAsignableException;
import ar.edu.utn.dds.k3003.model.Ruta;
import ar.edu.utn.dds.k3003.model.Traslado;
import ar.edu.utn.dds.k3003.repositories.*;
import io.javalin.http.HttpStatus;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Response;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Getter
@Setter
public class Fachada {

    private final RutaRepository rutaRepository;
    private final RutaMapper rutaMapper;
    private final TrasladoRepository trasladoRepository;
    private final TrasladoMapper trasladoMapper;
    private FachadaViandas fachadaViandas;
    private FachadaHeladeras fachadaHeladeras;
    private FachadaColaboradoresModificada fachadaColaboradores;

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public Fachada() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("entrega3_tp_dds");
        this.entityManager = entityManagerFactory.createEntityManager();
        this.rutaRepository = new RutaRepository(entityManager);
        this.rutaMapper = new RutaMapper();
        this.trasladoMapper = new TrasladoMapper();
        this.trasladoRepository = new TrasladoRepository(entityManager);
    }


    public RutaDTO agregar(RutaDTO rutaDTO) throws IllegalArgumentException{

        Ruta ruta = new Ruta(rutaDTO.getColaboradorId(), rutaDTO.getHeladeraIdOrigen(), rutaDTO.getHeladeraIdDestino());
        try {
            if(fachadaColaboradores.obtenerFormasColaborar(rutaDTO.getColaboradorId()).contains(FormasDeColaborarEnum.TRANSPORTADOR)){
                ruta = this.rutaRepository.save(ruta);
            } else{
                throw new IllegalArgumentException("No se puede crear la ruta porque el colaborador no es un transportador");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la ruta: " + e.getMessage(), e);
        }

        return rutaMapper.map(ruta);
    }


    public TrasladoDTO buscarXId(Long aLong) throws NoSuchElementException { //el traslado
        Traslado traslado = trasladoRepository.findById(aLong);

        TrasladoDTO trasDto = trasladoMapper.map(traslado);

        return trasDto;
    }


    public TrasladoDTO asignarTraslado(TrasladoDTO trasladoDTO) throws TrasladoNoAsignableException {

        List<String> viandasATrasladar = new ArrayList<>();

        trasladoDTO.getListQrViandas().forEach(qrVianda -> {
            ViandaDTO viandaDTO = fachadaViandas.buscarXQR(qrVianda);

            // Si alguna vianda no se encuentra, lanza la excepción
            if (viandaDTO == null) {
                try {
                    throw new TrasladoNoAsignableException("No se encontró la vianda con el QR proporcionado: " + qrVianda);
                } catch (TrasladoNoAsignableException e) {
                    throw new RuntimeException(e);
                }

            }

            // Agrega el código QR de la vianda encontrada a la lista
            viandasATrasladar.add(viandaDTO.getCodigoQR());
        });

        List<Ruta> rutasPosibles = this.rutaRepository.findByHeladeras(trasladoDTO.getHeladeraOrigen(), trasladoDTO.getHeladeraDestino());
        if (rutasPosibles == null || rutasPosibles.isEmpty()) {
            throw new TrasladoNoAsignableException("No se puede asignar traslado porque no hay una ruta");
        }

        Collections.shuffle(rutasPosibles);

        Ruta ruta = rutasPosibles.get(0);
        Traslado traslado = trasladoRepository.save(new Traslado(viandasATrasladar, ruta,
                EstadoTrasladoEnum.ASIGNADO, trasladoDTO.getFechaTraslado()));

        return this.trasladoMapper.map(traslado);
    }



    public List<TrasladoDTO> trasladosDeColaborador(Long aLong, Integer integer, Integer integer1) {
        List<Traslado> trasladosDeColaborador = this.trasladoRepository.findByColaboradorId(aLong,integer,integer);
        List<Traslado> trasladosPorMesYAnio = trasladosDeColaborador.stream()
                .filter(t -> t.getRuta().getColaboradorId().equals(aLong))
                .filter(x -> x.getFechaTraslado().getMonthValue() == integer)
                .filter(x -> x.getFechaTraslado().getYear() == integer1)
                .collect(Collectors.toList());


         List<TrasladoDTO> trasDtos = trasladosPorMesYAnio.stream().map(t -> trasladoMapper.map(t)).collect(Collectors.toList());

        return trasDtos;
    }


    public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {
            this.fachadaHeladeras = fachadaHeladeras;
    }


    public void setViandasProxy(FachadaViandas fachadaViandas) {
        this.fachadaViandas = fachadaViandas;
    }

    public void setColaboradoresProxy(FachadaColaboradoresModificada fachadaColaboradores) {
        this.fachadaColaboradores = fachadaColaboradores;
    }

    public void trasladoRetirado(Long aLong) {
        TrasladoDTO trasladoDto = buscarXId(aLong);

        List<RetiroDTO> retiros = new ArrayList<>();

        //genero el retiroDto por cada vianda
        trasladoDto.getListQrViandas().forEach(qrVianda -> {
            RetiroDTO retiroHeladera = new RetiroDTO(qrVianda, "123", trasladoDto.getHeladeraOrigen());
            retiros.add(retiroHeladera);
        });

        //hago los retiros
        retiros.forEach(retiro -> fachadaHeladeras.retirar(retiro));

        //ahora cambio el estado de la vianda por cada vianda que contiene el traslado
        trasladoDto.getListQrViandas().forEach(qrVianda -> fachadaViandas.modificarEstado(qrVianda, EstadoViandaEnum.EN_TRASLADO));

        this.trasladoRepository.modificarEstado(aLong,EstadoTrasladoEnum.EN_VIAJE);
    }


    public void trasladoDepositado(Long aLong) {
        //es un traslado que ya fue creado en trasladoRetirado y ahora lo tengo que buscar
        TrasladoDTO trasladoDTO = buscarXId(aLong);

        //la deposito en la heladera cada vianda
        trasladoDTO.getListQrViandas().forEach(qrVianda -> fachadaHeladeras.depositar(trasladoDTO.getHeladeraDestino(), qrVianda));


        //cambio el estado y la heladera de destino de cada vianda
        trasladoDTO.getListQrViandas().forEach(qrVianda -> {
            fachadaViandas.modificarEstado(qrVianda, EstadoViandaEnum.DEPOSITADA);
            fachadaViandas.modificarHeladera(qrVianda, trasladoDTO.getHeladeraDestino());
        });


        //cambio el estado del traslado
       this.trasladoRepository.modificarEstado(aLong, EstadoTrasladoEnum.ENTREGADO);

    }

    public List<Ruta> obtenerTodasLasRutas() {
       return this.rutaRepository.getRutas().stream().toList();
    }

    public void borrarTraslados(){

        Long maxTrasladoId = trasladoRepository.maximoId();

        for (Long trasladoId = 0L; trasladoId <= maxTrasladoId; trasladoId++) {
            trasladoRepository.borrarTraslado_Viandas(trasladoId);
        }

        this.trasladoRepository.borrarTraslados();
    }

    public void borrarRutas(){
        this.rutaRepository.borrarRutas();
    }
}
