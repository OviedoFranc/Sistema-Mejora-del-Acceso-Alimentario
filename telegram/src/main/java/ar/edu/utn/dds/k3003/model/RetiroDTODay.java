package ar.edu.utn.dds.k3003.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RetiroDTODay {

    private Integer id;
    private String qrVianda;
    private String tarjeta;
    private String fechaRetiro;
    private Integer heladeraId;



    // Constructor
    @JsonCreator
    public RetiroDTODay(@JsonProperty("id") Integer id, @JsonProperty("qrVianda") String qrVianda, @JsonProperty("tarjeta") String tarjeta, @JsonProperty("fechaRetiro") String fechaRetiro, @JsonProperty("heladeraId") Integer heladeraId) {
      this.id = id;
      this.qrVianda = qrVianda;
      this.tarjeta = tarjeta;
      this.fechaRetiro = fechaRetiro;
      this.heladeraId = heladeraId;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQrVianda() {
      return qrVianda;
    }

    public void setQrVianda(String qrVianda) {
      this.qrVianda = qrVianda;
    }

    public String getTarjeta() {
      return tarjeta;
    }

    public void setTarjeta(String tarjeta) {
      this.tarjeta = tarjeta;
    }

    public String getFechaRetiro() {
      return fechaRetiro;
    }

    public void setFechaRetiro(String fechaRetiro) {
      this.fechaRetiro = fechaRetiro;
    }

    public Integer getHeladeraId() {
      return heladeraId;
    }

    public void setHeladeraId(Integer heladeraId) {
      this.heladeraId = heladeraId;
    }

    // ToString
    @Override
    public String toString() {
      return "RetiroDTODay{" +
          "qrVianda='" + qrVianda + '\'' +
          ", tarjeta='" + tarjeta + '\'' +
          ", fechaRetiro=" + fechaRetiro +
          ", heladeraId=" + heladeraId +
          '}';
    }
}