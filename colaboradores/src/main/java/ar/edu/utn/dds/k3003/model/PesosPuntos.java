package ar.edu.utn.dds.k3003.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
/*
@Setter
@Getter
@Entity
@Table(name= "pesosPuntos")*/
public class PesosPuntos {

  //  @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Para que no moleste

   // @Column(name = "pesosDonados")
    private Double pesosDonados=0.0;
   // @Column(name = "viandasDistribuidas")
    private Double viandasDistribuidas=0.0;
   // @Column(name = "viandasDonadas")
    private Double viandasDonadas=0.0;
  //  @Column(name = "tarjetasRepartidas")
    private Double tarjetasRepartidas=0.0;
  //  @Column(name = "heladerasActivas")
    private Double heladerasActivas=0.0;
  //  @Column(name = "heladerasReparadas")
    private Double heladerasReparadas=0.0;

    public PesosPuntos(){}
    public PesosPuntos(Double pesosDonados,Double viandas_Distribuidas,Double viandasDonadas,Double tarjetasRepartidas,Double heladerasActivas,Double heladerasReparadas){
        this.id=null;
        this.pesosDonados=pesosDonados;
        this.viandasDistribuidas=viandas_Distribuidas;
        this.viandasDonadas=viandasDonadas;
        this.tarjetasRepartidas=tarjetasRepartidas;
        this.heladerasActivas=heladerasActivas;
        this.heladerasReparadas=heladerasReparadas;
    }

    public void setPuntos(Double pesosDonados,Double viandas_Distribuidas,Double viandasDonadas,Double tarjetasRepartidas,Double heladerasActivas,Double heladerasReparadas){
        this.pesosDonados=pesosDonados;
        this.viandasDistribuidas=viandas_Distribuidas;
        this.viandasDonadas=viandasDonadas;
        this.tarjetasRepartidas=tarjetasRepartidas;
        this.heladerasActivas=heladerasActivas;
        this.heladerasReparadas=heladerasReparadas;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {return this.id;}
    public Double getPesosDonados() {
        return this.pesosDonados;
    }
    public Double getViandas_Distribuidas() {
        return this.viandasDistribuidas;
    }
    public Double getViandasDonadas() {
        return this.viandasDonadas;
    }
    public Double getTarjetasRepartidas() {
        return this.tarjetasRepartidas;
    }
    public Double getHeladerasActivas() {
        return this.heladerasActivas;
    }
    public Double getHeladerasReparadas() {
        return this.heladerasReparadas;
    }
}
