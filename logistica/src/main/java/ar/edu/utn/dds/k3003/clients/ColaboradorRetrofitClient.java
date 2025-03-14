package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.Service.FormasDeColaborarEnum;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface ColaboradorRetrofitClient {

    @GET("/colaboradores/{id}/formas")
    Call<List<FormasDeColaborarEnum>> get(@Path("id") Long id);

}
