package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.model.SuscripcionDTO2;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class HeladeraProxy implements FachadaHeladera {

    private final String endpoint;
    private final HeladeraRetrofitClient service;

    public HeladeraProxy(ObjectMapper objectMapper) {
        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_HELADERAS", "https://heladeras-tlcz.onrender.com"); //CUIDADO

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(HeladeraRetrofitClient.class);

    }

    @SneakyThrows
    public void postSubscripcion(Integer heladeraId, SuscripcionDTO2 suscripcionDTO) throws NoSuchElementException{

       // Response<List<TrasladoDTO>> execute = null;
        try {
             service.postSubscripcion(suscripcionDTO).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
      /*  if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
            throw new NoSuchElementException("No se encontro la heladera");
        }*/
    }

    @SneakyThrows
    public void getReportarFalla(Integer healderaId) throws NoSuchElementException{

        try {
            service.getReportarFalla(healderaId).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void getArreglarFalla(Integer healderaId) throws NoSuchElementException{

        try {
            service.getArreglarFalla(healderaId).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public HeladeraDTO agregar(HeladeraDTO heladeraDTO) {
        return null;
    }

    @Override
    public void depositar(Integer integer, String s) throws NoSuchElementException {

    }

    @Override
    public Integer cantidadViandas(Integer integer) throws NoSuchElementException {
        return null;
    }

    @Override
    public void retirar(RetiroDTO retiroDTO) throws NoSuchElementException {

    }

    @Override
    public void temperatura(TemperaturaDTO temperaturaDTO) {

    }

    @Override
    public List<TemperaturaDTO> obtenerTemperaturas(Integer integer) {
        return null;
    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {

    }
}
