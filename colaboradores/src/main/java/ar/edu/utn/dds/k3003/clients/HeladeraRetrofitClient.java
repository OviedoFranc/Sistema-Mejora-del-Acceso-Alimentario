package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.model.SuscripcionDTO2;
import retrofit2.Call;
import retrofit2.http.*;

public interface HeladeraRetrofitClient {

    @GET("/heladeras/{heladeraId}/reportarFalla")
    Call<Void>  getReportarFalla(@Path("heladeraId") Integer healderaId);

    @GET("/heladeras/{heladeraId}/arreglarFalla")
     Call<Void>getArreglarFalla(@Path("heladeraId") Integer healderaId);

    @POST("/suscripciones")
    Call<Void> postSubscripcion(@Body SuscripcionDTO2 body);

}
