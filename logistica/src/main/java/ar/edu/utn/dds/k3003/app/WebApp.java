package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.Controller.RutaController;
import ar.edu.utn.dds.k3003.Controller.TrasladoController;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WebApp {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        var URL_VIANDAS = dotenv.get("URL_VIANDAS");
        var URL_HELADERAS = dotenv.get("URL_HELADERAS");
        var URL_COLABORADORES = dotenv.get("URL_COLABORADORES");

        var objectMapper = createObjectMapper();
        var fachada = new Fachada();

        fachada.setViandasProxy(new ar.edu.utn.dds.k3003.clients.ViandasProxy(objectMapper));
        fachada.setHeladerasProxy(new ar.edu.utn.dds.k3003.clients.HeladerasProxy(objectMapper));
        fachada.setColaboradoresProxy(new ar.edu.utn.dds.k3003.clients.ColaboradorProxy(objectMapper));

        var port = Integer.parseInt(dotenv.get("PORT"));

        var app = Javalin.create().start(port);

        var rutaController = new RutaController(fachada);
        var trasladosController = new TrasladoController(fachada);

        app.post("/rutas", rutaController::agregar);
        app.post("/traslados", trasladosController::asignar);
        app.get("/rutas", rutaController::obtenerTodas);
        app.get("/traslados/{id}", trasladosController::obtener);
        app.get( "/traslados/search/findByColaboradorId", trasladosController::ObtenerTrasladosColaborador);
        app.patch("/traslados/{id}", trasladosController::modificarEstado);
        app.delete("/traslados", trasladosController::deleteAll);
        app.delete("/rutas", rutaController::deleteAllRutas);

    }

    public static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        configureObjectMapper(objectMapper);
        return objectMapper;
    }

    public static void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
    }
}