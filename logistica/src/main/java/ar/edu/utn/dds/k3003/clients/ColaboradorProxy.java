package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.Service.FachadaColaboradoresModificada;
import ar.edu.utn.dds.k3003.Service.FormasDeColaborarEnum;
import ar.edu.utn.dds.k3003.facades.FachadaColaboradores;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class ColaboradorProxy implements FachadaColaboradoresModificada {
    private final String endpoint;
    private final ColaboradorRetrofitClient service;
    private static ColaboradorProxy instancia = null;
    private FachadaColaboradores fachadaColaboradores;
    public ColaboradorProxy(ObjectMapper objectMapper) {

        this.endpoint = System.getenv("URL_COLABORADOR");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(ColaboradorRetrofitClient.class);
    }
    @Override
    public ColaboradorDTO agregar(ColaboradorDTO colaboradorDTO) {
        return null;
    }

    @Override
    public ColaboradorDTO buscarXId(Long aLong) throws NoSuchElementException {
        return null;
    }

    @Override
    public Double puntos(Long aLong) throws NoSuchElementException {
        return null;
    }

    @Override
    public ColaboradorDTO modificar(Long aLong, List<FormaDeColaborarEnum> list) throws NoSuchElementException {
        return null;
    }

    @Override
    public void actualizarPesosPuntos(Double aDouble, Double aDouble1, Double aDouble2, Double aDouble3, Double aDouble4) {

    }

    public List<FormasDeColaborarEnum> obtenerFormasColaborar(Long id) throws IOException {
        Response<List<FormasDeColaborarEnum>> execute = service.get(id).execute();

        if (execute.isSuccessful()) {
            return execute.body();
        }
        if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
            throw new NoSuchElementException("error al buscar las formas de colaborar");
        }
        throw new RuntimeException("Error conectandose con el componente colaboradores");



    }

    @Override
    public void setLogisticaProxy(FachadaLogistica fachadaLogistica) {

    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {

    }
}
