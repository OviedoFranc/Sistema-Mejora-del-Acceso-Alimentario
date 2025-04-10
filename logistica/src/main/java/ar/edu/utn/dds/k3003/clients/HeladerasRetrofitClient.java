package ar.edu.utn.dds.k3003.clients;


import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.facades.dtos.RetiroDTO;
import retrofit2.Call;
import retrofit2.http.*;

public interface HeladerasRetrofitClient {
    @GET ("/heladeras/{heladeraId}")
    Call<HeladeraDTO> obtenerHeladera(@Path("heladeraId") Integer heladeraId);

    @POST("/retiros")
    Call<Void> retirar(@Body RetiroDTO retiro);

    @POST("/depositos/{heladeraId}/{qrVianda}")
    Call<Void> depositar(@Path("heladeraId") Integer heladeraId, @Path("qrVianda") String qrVianda);


}
