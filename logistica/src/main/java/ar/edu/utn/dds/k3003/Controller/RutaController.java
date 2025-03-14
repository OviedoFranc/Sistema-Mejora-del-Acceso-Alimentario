package ar.edu.utn.dds.k3003.Controller;

import ar.edu.utn.dds.k3003.Service.UtilsMetrics;
import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.model.Ruta;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RutaController {
//
    private Fachada fachada;
    public RutaController(Fachada fachada) {
        this.fachada = fachada;
    }

    public void agregar(Context context) throws IllegalArgumentException {
        try{
            var rutaDTO = context.bodyAsClass(RutaDTO.class);
            var rutaDTORta = this.fachada.agregar(rutaDTO);
            context.json(rutaDTORta);
            context.status(HttpStatus.CREATED);
        } catch (IllegalArgumentException e){
            context.result("Error al crear las rutas porque el colaborador no es un trnsportador: " + e.getMessage());
            context.status(HttpStatus.BAD_REQUEST);;
        } catch (Exception e) {
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
            context.result("Error al crear la ruta debido a un error inesperado: " + e.getMessage());
        }
    }

    public void obtenerTodas(Context context) {
        var rutas = this.fachada.obtenerTodasLasRutas();
        context.json(rutas);
        context.status(HttpStatus.OK);
    }

    public void deleteAllRutas(Context context) {
        try {
            this.fachada.borrarRutas();
            context.result("Todas las rutas han sido eliminados con éxito.");
            context.status(HttpStatus.OK);
        } catch (Exception e) {
            context.result("Error al eliminar las rutas: " + e.getMessage());
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
