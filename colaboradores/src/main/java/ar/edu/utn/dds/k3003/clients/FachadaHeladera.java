package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.model.SuscripcionDTO2;

import java.util.List;
import java.util.NoSuchElementException;

public interface FachadaHeladera {
        HeladeraDTO agregar(HeladeraDTO var1);

        void depositar(Integer var1, String var2) throws NoSuchElementException;

        Integer cantidadViandas(Integer var1) throws NoSuchElementException;

        void retirar(RetiroDTO var1) throws NoSuchElementException;

        void temperatura(TemperaturaDTO var1);

        List<TemperaturaDTO> obtenerTemperaturas(Integer var1);

    void getReportarFalla(Integer heladeraId);

    void getArreglarFalla(Integer heladeraId);

    void postSubscripcion(Integer heladeraId, SuscripcionDTO2 body);

    void setViandasProxy(FachadaViandas var1);

    }