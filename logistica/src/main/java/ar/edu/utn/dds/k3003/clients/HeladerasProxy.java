package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

    public class HeladerasProxy implements FachadaHeladeras {

        private final String endpoint;
        private final HeladerasRetrofitClient service;

        public HeladerasProxy(ObjectMapper objectMapper) {

            this.endpoint = System.getenv("URL_HELADERA");

            var retrofit =
                    new Retrofit.Builder()
                            .baseUrl(this.endpoint)
                            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                            .build();

            this.service = retrofit.create(HeladerasRetrofitClient.class);
        }

        @Override
        public HeladeraDTO agregar(HeladeraDTO heladeraDTO) {
            return null;
        }

        public HeladeraDTO obtenerHeladera(Integer heladeraId){
            try {
                Response<HeladeraDTO> response = service.obtenerHeladera(heladeraId).execute();

                if (response.isSuccessful()) {
                    return response.body();
                } else if (response.code() == HttpStatus.NOT_FOUND.getCode()) {
                        throw new NoSuchElementException("No se encontró la heladera: " + heladeraId);
                    } else {
                        throw new RuntimeException("Error al realizar el deposito: " + response.errorBody().string());
                    }
            } catch (IOException e) {
                throw new RuntimeException("Error en la comunicación con el servicio de heladeras", e);
            }
        }

        @Override
        public void depositar(Integer integer, String s) throws NoSuchElementException {
          try {
                Response<Void> response = service.depositar(integer,s).execute();

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
            return 0;
        }

        @Override
        public void retirar(RetiroDTO retiroDTO) throws NoSuchElementException {
            try {
                Response<Void> response = service.retirar(retiroDTO).execute();

                if (!response.isSuccessful()) {
                    if (response.code() == HttpStatus.NOT_FOUND.getCode()) {
                        throw new NoSuchElementException("No se encontró la heladera para el retiro: " + retiroDTO.getHeladeraId());
                    } else {
                        throw new RuntimeException("Error al realizar el retiro: " + response.errorBody().string());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error en la comunicación con el servicio de heladeras", e);
            }
        }

        @Override
        public void temperatura(TemperaturaDTO temperaturaDTO) {

        }

        @Override
        public List<TemperaturaDTO> obtenerTemperaturas(Integer integer) {
            return List.of();
        }

        @Override
        public void setViandasProxy(FachadaViandas fachadaViandas) {

        }
    }

