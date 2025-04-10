package ar.edu.utn.dds.k3003.model;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.util.List;

public final class ColaboradorDTO {
    private Long id;
    private String nombre;
    private List<FormasDeColaborarEnum> formas;

    public ColaboradorDTO(String nombre, List<FormasDeColaborarEnum> formas) {
        this.nombre = nombre;
        this.formas = formas;
    }

    public Long getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public List<FormasDeColaborarEnum> getFormas() {
        return this.formas;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFormas(List<FormasDeColaborarEnum> formas) {
        this.formas = formas;
    }

    protected ColaboradorDTO() {
    }
}
