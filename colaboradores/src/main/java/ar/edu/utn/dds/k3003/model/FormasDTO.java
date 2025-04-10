package ar.edu.utn.dds.k3003.model;

import java.util.List;

public class FormasDTO {
    private List<FormasDeColaborarEnum> formas;

    public List<FormasDeColaborarEnum> getFormas() {
        return formas;
    }

    public void setFormas(List<FormasDeColaborarEnum> formasDeColaborar) {
        this.formas = formasDeColaborar;
    }
}