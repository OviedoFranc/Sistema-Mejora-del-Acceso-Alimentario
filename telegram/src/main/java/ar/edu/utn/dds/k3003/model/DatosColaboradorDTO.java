package ar.edu.utn.dds.k3003.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class DatosColaboradorDTO {
	
    private Integer id;
    private String nombre;
    private List<String> formas;
    private Integer pesosDonados;
    private Integer heladerasReparadas;

    @JsonCreator
    public DatosColaboradorDTO(@JsonProperty("id") Integer id,@JsonProperty("nombre") String nombre,@JsonProperty("formas") List<String> formas,@JsonProperty("pesosDonados") Integer pesosDonados,@JsonProperty("heladerasReparadas") Integer heladerasReparadas) {
      this.id = id;
      this.nombre = nombre;
      this.formas = formas;
      this.pesosDonados=pesosDonados;
      this.heladerasReparadas=heladerasReparadas;
    }

    public Integer getId() {
        return id;
    }
    public Integer getPesosDonados() {
        return pesosDonados;
    }
    public Integer getHeladerasReparadas() {return heladerasReparadas;}
    
    public void setId(Integer id) {
      this.id = id;
    }
    
    public String getNombre() {
      return nombre;
    }
    
    public void setNombre(String nombre) {
      this.nombre = nombre;
    }
    
    public List<String> getFormas() {
      return formas;
    }
    
    public void setFormas(List<String> formas) {
      this.formas = formas;
    }

    
    @Override
    public String toString() {
      return "DatosColaboradorDTO{" +
          "id='" + id + '\'' +
          ", nombre='" + nombre + '\'' +
          ", formas=" + formas +
          '}';
    }
}