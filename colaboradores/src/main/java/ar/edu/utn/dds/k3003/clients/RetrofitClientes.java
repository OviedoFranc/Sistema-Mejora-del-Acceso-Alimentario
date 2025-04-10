package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import retrofit2.Call;

import java.util.List;

public class RetrofitClientes implements LogisticaRetrofitClient, ViandasRetrofitClient{

    @Override
    public Call<List<TrasladoDTO>> getTraslados(Long id, Integer mes, Integer anio) {
        return null;
    }
    @Override
    public Call<List<ViandaDTO>> getViandas(Long id, Integer mes, Integer anio) {
        return null;
    }
}
