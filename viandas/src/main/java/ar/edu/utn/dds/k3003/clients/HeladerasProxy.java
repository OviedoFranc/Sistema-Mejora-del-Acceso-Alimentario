package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import io.javalin.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class HeladerasProxy implements FachadaHeladeras {

    private final String endpoint;
    private final HeladerasRetrofitClient service;

    public HeladerasProxy(ObjectMapper objectMapper) {
        var env = System.getenv();
        this.endpoint = env.get("URL_HELADERA");

        var retrofit = new Retrofit.Builder()
                .baseUrl(this.endpoint)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        this.service = retrofit.create(HeladerasRetrofitClient.class);
    }

    @Override
    public HeladeraDTO agregar(HeladeraDTO heladeraDTO) {
        return null;
    }

    @Override
    public void depositar(Integer integer, String s) throws NoSuchElementException {
    	Response<Void> response;
        try {
              response = service.depositar(integer,s).execute();

              if (!response.isSuccessful()) {
                  if (response.code() == HttpStatus.NOT_FOUND.getCode()) {
                      throw new NoSuchElementException("No se encontró la heladera para el deposito: " + integer);
                  } else {
                      throw new RuntimeException("Error al realizar el deposito: " + response.errorBody().string());
                  }
              }
          } catch (IOException e) {
              throw new RuntimeException("Error en la comunicación con el servicio de heladeras", e);
          }

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
    public List<TemperaturaDTO> obtenerTemperaturas(Integer heladeraId) {
    	Response<List<TemperaturaDTO>> response;
        try {
        	response = service.obtenerTemperaturas(heladeraId).execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            if (response.code() == HttpStatus.NOT_FOUND.getCode()) {
                throw new NoSuchElementException("No se encontro la heladera " + heladeraId);
            }
            throw new NoSuchElementException("No se pudo conseguir las temperaturas de la heladera " + heladeraId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las temperaturas de la heladera", e);
        }
    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {

    }

}
