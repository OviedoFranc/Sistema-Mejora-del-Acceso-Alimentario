package ar.edu.utn.dds.k3003.Utils;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class TrasladoDTO {
    private Long id;
    private List<String> listQrViandas;
    private EstadoTrasladoEnum status;
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )
    private LocalDateTime fechaTraslado;
    private Integer heladeraOrigen;
    private Integer heladeraDestino;
    private Long colaboradorId;

    public TrasladoDTO(List<String> listQrViandas, EstadoTrasladoEnum status, LocalDateTime fechaTraslado, Integer heladeraOrigen, Integer heladeraDestino) {
        this.listQrViandas = listQrViandas;
        this.status = status;
        this.fechaTraslado = fechaTraslado;
        this.heladeraOrigen = heladeraOrigen;
        this.heladeraDestino = heladeraDestino;
    }

    public TrasladoDTO(List<String> listQrViandas, Integer heladeraOrigen, Integer heladeraDestino) {
        this(listQrViandas, EstadoTrasladoEnum.CREADO, LocalDateTime.now(), heladeraOrigen, heladeraDestino);
    }

    public Long getId() {
        return this.id;
    }

    public List<String> getListQrViandas() {
        return this.listQrViandas;
    }

    public EstadoTrasladoEnum getStatus() {
        return this.status;
    }

    public LocalDateTime getFechaTraslado() {
        return this.fechaTraslado;
    }

    public Integer getHeladeraOrigen() {
        return this.heladeraOrigen;
    }

    public Integer getHeladeraDestino() {
        return this.heladeraDestino;
    }

    public Long getColaboradorId() {
        return this.colaboradorId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setListQrVianda(List<String> listQrViandas) {
        this.listQrViandas = listQrViandas;
    }

    public void setStatus(EstadoTrasladoEnum status) {
        this.status = status;
    }

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )
    public void setFechaTraslado(LocalDateTime fechaTraslado) {
        this.fechaTraslado = fechaTraslado;
    }

    public void setHeladeraOrigen(Integer heladeraOrigen) {
        this.heladeraOrigen = heladeraOrigen;
    }

    public void setHeladeraDestino(Integer heladeraDestino) {
        this.heladeraDestino = heladeraDestino;
    }

    public void setColaboradorId(Long colaboradorId) {
        this.colaboradorId = colaboradorId;
    }

    public TrasladoDTO(){

    }

}
