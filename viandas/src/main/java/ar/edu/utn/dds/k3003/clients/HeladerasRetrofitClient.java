package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface HeladerasRetrofitClient {
    @GET("heladeras/{heladeraId}/temperaturas")
    Call<List<TemperaturaDTO>> obtenerTemperaturas(@Path("heladeraId") Integer heladeraId);
    
    @POST("/depositos/{heladeraId}/{qrVianda}")
    Call<Void> depositar(@Path("heladeraId") Integer heladeraId, @Path("qrVianda") String qrVianda);
}
