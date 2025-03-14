package ar.edu.utn.dds.k3003.model;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name="colaborador")
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Es como el id de las tarjetas
    @Column (name = "nombre")
    private String nombre;
/*
    @ElementCollection(targetClass = FormaDeColaborarEnum.class)
    @Enumerated(EnumType.STRING) // Puedes usar EnumType.ORDINAL si prefieres almacenar índices
    @CollectionTable(name = "colaborador_formas_de_colaborar", joinColumns = @JoinColumn(name = "colaborador_id"))
    @Column(name = "forma_de_colaborar")*/
    @Column(name="formas")
    @Convert(converter = ConversorFormasDeColaborar.class)
    private List<FormasDeColaborarEnum> formas;

    @Column (name="pesosDonados")
    private Integer pesosDonados=0;

    @Column (name="heladerasReparadas")
    private Integer heladerasReparadas=0;

    //@Column(name="suscripcion")
    //private List<TipoSuscripcion>  suscripcion;
/*
    @Column(name="cantidadFaltante")
    private Integer cantidadFaltante;

    @Column(name="cantidadDisponible")
    private Integer cantidadDisponible;
*/
    // private Long tarjetasRepartidas; En teoria no se usa esta entrega
    public Colaborador(){}
    public Colaborador(Long id, String nombre,List<FormasDeColaborarEnum> list) {
        this.id = id;
        this.nombre = nombre;
        this.formas=list;
        this.pesosDonados=0;
        this.heladerasReparadas=0;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFormas(List<FormasDeColaborarEnum> list) {
        this.formas = list;
    }

    /*public void setSuscripcion(List<TipoSuscripcion> suscripcion) {
        this.suscripcion = suscripcion;
    }*/

    //public void setCantidadFaltante(Integer cantidadFaltante) {this.cantidadFaltante = cantidadFaltante;}

   // public void setCantidadDisponible(Integer cantidadDisponible) {this.cantidadDisponible = cantidadDisponible;}

    public void sumarPesos(Integer pesos) {this.pesosDonados+=pesos;}

    public void sumarheladerasReparadas() {this.heladerasReparadas++;}

    public Long getId() {return this.id;}

    public List<FormasDeColaborarEnum> getFormas() {return this.formas;}

    public String getNombre() {
        return this.nombre;
    }

    public Integer getPesosDonados() {
        return this.pesosDonados;
    }

    public Integer getHeladerasReparadas() {
        return this.heladerasReparadas;
    }

   // public List<TipoSuscripcion> getSuscripcion() {return this.suscripcion;}

   // public Integer getCantidadFaltante() {return this.cantidadFaltante;}

   // public Integer getCantidadDisponible() {return this.cantidadDisponible;}
}
