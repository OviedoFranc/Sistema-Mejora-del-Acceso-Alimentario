package ar.edu.utn.dds.k3003.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IncidenteDTO {

        public String tipoIncidente;
        public Integer incidenteId;
        public String fechaIncidente;

        @JsonCreator
        public IncidenteDTO(@JsonProperty("tipoIncidente") String tipoIncidente,  @JsonProperty("incidenteId") Integer incidenteId, @JsonProperty("fechaIncidente") String fechaIncidente) {
            this.tipoIncidente = tipoIncidente;
            this.incidenteId = incidenteId;
            this.fechaIncidente = fechaIncidente;
        }

    public String getTipoIncidente() {
        return tipoIncidente;
    }

    public Integer getIncidenteId() {
        return incidenteId;
    }

    public String getFechaIncidente() {
        return fechaIncidente;
    }
}
