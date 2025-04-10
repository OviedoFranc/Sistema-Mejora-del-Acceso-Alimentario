package ar.edu.utn.dds.k3003.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SuscripcionDTO2 {

    private Long colaboradorId; // Es como el id de las tarjetas
    private Integer heladeraId;
    private TipoSuscripcion tipoSuscripcion;
    private Integer cantidadN=0;

    public SuscripcionDTO2(){}
    public SuscripcionDTO2(Long Colaboradorid, Integer heladeraId, TipoSuscripcion tipoSuscripcion, Integer cantidadN) {
        this.colaboradorId = Colaboradorid;
        this.heladeraId = heladeraId;
        this.tipoSuscripcion=tipoSuscripcion;
        this.cantidadN=cantidadN;
    }
    public SuscripcionDTO2(Long Colaboradorid, Integer heladeraId, TipoSuscripcion tipoSuscripcion) {
        this.colaboradorId = Colaboradorid;
        this.heladeraId = heladeraId;
        this.tipoSuscripcion=tipoSuscripcion;
    }

    public Long getId() {return this.colaboradorId;}
    public Integer getHeladeraId() {return this.heladeraId;}
    public Integer getCantidadN() {return this.cantidadN;}
    public TipoSuscripcion getTipoSuscripcion() {return this.tipoSuscripcion;}


}
