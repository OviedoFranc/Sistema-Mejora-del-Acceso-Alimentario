package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.Utils.TrasladoDTO;
import ar.edu.utn.dds.k3003.model.Traslado;

public class TrasladoMapper {

    public TrasladoDTO map(Traslado traslado) {
        TrasladoDTO trasladoDTO = new TrasladoDTO(traslado.getListaQrVianda(), traslado.getEstado(),
                traslado.getFechaTraslado(), traslado.getRuta().getHeladeraIdOrigen(),
                traslado.getRuta().getHeladeraIdDestino());

        trasladoDTO.setId(traslado.getId());
        trasladoDTO.setColaboradorId(traslado.getRuta().getColaboradorId());


        return trasladoDTO;
    }

//

}
