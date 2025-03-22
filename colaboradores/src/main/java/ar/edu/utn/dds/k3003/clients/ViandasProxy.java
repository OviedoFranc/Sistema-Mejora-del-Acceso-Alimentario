package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
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

public class ViandasProxy implements FachadaViandas {

  private final String endpoint;
  private final ViandasRetrofitClient service;

  public ViandasProxy(ObjectMapper objectMapper) {
    var env = System.getenv();
    this.endpoint = env.get("URL_VIANDA");

    var retrofit =
            new Retrofit.Builder()
                    .baseUrl(this.endpoint)
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                    .build();

    this.service = retrofit.create(ViandasRetrofitClient.class);
  }

  @Override
  public ViandaDTO agregar(ViandaDTO viandaDTO) {
    return null;
  }

  @Override
  public ViandaDTO modificarEstado(String QR, EstadoViandaEnum estadoViandaEnum) {
    ViandaDTO vianda = this.buscarXQR(QR);
    vianda.setEstado(estadoViandaEnum);
    return vianda;
  }

  @SneakyThrows
  @Override
  public List<ViandaDTO> viandasDeColaborador(Long id, Integer mes, Integer anio) throws NoSuchElementException {
      Response<List<ViandaDTO>> execute = null;
      try {
          execute = service.getViandas(id, mes, anio).execute();
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      if (execute.isSuccessful()) {
            return execute.body();
    }
    if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
      return new ArrayList<>();
    }
    throw new RuntimeException("Error conectandose con el componente viandas");
  }

  @Override
  public ViandaDTO buscarXQR(String qr) throws NoSuchElementException {
    return null;
  }

  @Override
  public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {}

  @Override
  public boolean evaluarVencimiento(String s) throws NoSuchElementException {
    return false;
  }

  @Override
  public ViandaDTO modificarHeladera(String s, int i) {
    return null;
  }
}
