package ar.edu.utn.dds.k3003.Controller;

import ar.edu.utn.dds.k3003.Service.UtilsMetrics;
import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.Utils.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
//
public class TrasladoController {
    private Fachada fachada;
    public TrasladoController(Fachada fachada) {
        this.fachada = fachada;
    }

    public void asignar(Context context) {
        try {
            var trasladoDTO = this.fachada.asignarTraslado(context.bodyAsClass(TrasladoDTO.class));
            context.json(trasladoDTO);
        } catch (NoSuchElementException |
                 ar.edu.utn.dds.k3003.exceptions.TrasladoNoAsignableException e) {
            context.result(e.getLocalizedMessage());
            context.status(HttpStatus.BAD_REQUEST);
        }
    }

    public void obtener(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var trasladoDTO = this.fachada.buscarXId(id);
            context.json(trasladoDTO);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void ObtenerTrasladosColaborador(Context context){
        var id = context.queryParamAsClass("id", Long.class).get();
        var anio = context.queryParamAsClass("anio", Integer.class).get();
        var mes = context.queryParamAsClass("mes", Integer.class).get();
        try {
            var trasladoDTO = this.fachada.trasladosDeColaborador(id,mes,anio);
            context.json(trasladoDTO);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }

    }

    public void modificarEstado(Context context) {
        var idTraslado = context.pathParamAsClass("id", Long.class).get();
       // var idTraslado = context.queryParamAsClass("id", Long.class).get();

        try {
            TrasladoDTO trasladoDTO = context.bodyAsClass(TrasladoDTO.class);
            trasladoDTO.setId(idTraslado);
            String nuevoEstado = trasladoDTO.getStatus().toString();

            if (nuevoEstado == "ENTREGADO") {
                this.fachada.trasladoDepositado(idTraslado);
                UtilsMetrics.enviarNuevoTrasladoRealizado();
                UtilsMetrics.actualizarTrasladosEnCurso(idTraslado, false);
            }
            if (nuevoEstado == "EN_VIAJE") {
                UtilsMetrics.actualizarTrasladosEnCurso(idTraslado, true);
                this.fachada.trasladoRetirado(idTraslado);

            }
        }
        catch(NoSuchElementException ex) {
                context.result(ex.getLocalizedMessage());
                context.status(HttpStatus.NOT_FOUND);

            }
    }


    public void deleteAll(Context context) {
        try {
            this.fachada.borrarTraslados();
            context.result("Todos los traslados han sido eliminados con éxito.");
            context.status(HttpStatus.OK);
        } catch (Exception e) {
            context.result("Error al eliminar los traslados: " + e.getMessage());
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
