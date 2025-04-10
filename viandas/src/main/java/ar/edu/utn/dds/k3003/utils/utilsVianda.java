package ar.edu.utn.dds.k3003.utils;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.repositories.ViandaMapper;
import ar.edu.utn.dds.k3003.model.Vianda;
import java.time.LocalDateTime;
import java.util.List;

public class utilsVianda {

    public static void crearViandasGenericas(Fachada fachada){
            ViandaDTO viandaNuevaDTO1 = new ViandaDTO("asd", LocalDateTime.now(),EstadoViandaEnum.PREPARADA,1L,1);
            ViandaDTO viandaNuevaDTO2 = new ViandaDTO("fgh", LocalDateTime.now(),EstadoViandaEnum.PREPARADA,2L,2);
            ViandaDTO viandaNuevaDTO3 = new ViandaDTO("jkl", LocalDateTime.now(),EstadoViandaEnum.PREPARADA,3L,3);
            fachada.agregar(viandaNuevaDTO1);
            fachada.agregar(viandaNuevaDTO2);
            fachada.agregar(viandaNuevaDTO3);
    }
    public static void crearViandasGenericasYDepositarlas(Fachada fachada){
    	ViandaDTO viandaNuevaDTO4 = new ViandaDTO("qwe", LocalDateTime.now(),EstadoViandaEnum.PREPARADA,1L,1);
        ViandaDTO viandaNuevaDTO5 = new ViandaDTO("rty", LocalDateTime.now(),EstadoViandaEnum.PREPARADA,2L,2);
        ViandaDTO viandaNuevaDTO6 = new ViandaDTO("uio", LocalDateTime.now(),EstadoViandaEnum.PREPARADA,3L,3);
        fachada.agregarYDepositar(viandaNuevaDTO4);
        fachada.agregarYDepositar(viandaNuevaDTO5);
        fachada.agregarYDepositar(viandaNuevaDTO6);
    }
}