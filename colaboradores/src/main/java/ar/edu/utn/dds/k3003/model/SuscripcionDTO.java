package ar.edu.utn.dds.k3003.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public final class SuscripcionDTO {

    private Long colaboradorId; // Es como el id de las tarjetas
    private Integer heladeraId;
    private List<TipoSuscripcion> tipoSuscripcion;
    private Integer cantidadFaltante;
    private Integer cantidadDisponible;

    public SuscripcionDTO(Long colaboradorId, Integer heladeraId, List<TipoSuscripcion> tipoSuscripcion,Integer cantidadFaltante,Integer cantidadDisponible) {
        this.colaboradorId = colaboradorId;
        this.heladeraId = heladeraId;
        this.tipoSuscripcion=tipoSuscripcion;
        this.cantidadFaltante =cantidadFaltante;
        this.cantidadDisponible=cantidadDisponible;
    }

    public void setId(Long id) {this.colaboradorId=id;}
    public Long getId() {return this.colaboradorId;}
    public Integer getHeladeraId() {return this.heladeraId;}
    public Integer getCantidadFaltante() {return this.cantidadFaltante;}
    public Integer getCantidadDisponible() {return this.cantidadDisponible;}
    public List<TipoSuscripcion> getTipoSuscripcion() {return this.tipoSuscripcion;}
    protected SuscripcionDTO(){}

}
