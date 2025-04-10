package ar.edu.utn.dds.k3003.Service;

import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public interface FachadaColaboradoresModificada {
    ColaboradorDTO agregar(ColaboradorDTO var1);

    ColaboradorDTO buscarXId(Long var1) throws NoSuchElementException;

    Double puntos(Long var1) throws NoSuchElementException;

    ColaboradorDTO modificar(Long var1, List<FormaDeColaborarEnum> var2) throws NoSuchElementException;

    void actualizarPesosPuntos(Double var1, Double var2, Double var3, Double var4, Double var5);

    void setLogisticaProxy(FachadaLogistica var1);

    void setViandasProxy(FachadaViandas var1);

    List<FormasDeColaborarEnum> obtenerFormasColaborar(Long id) throws IOException;
}
