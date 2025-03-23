package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.Controller.RutaController;
import ar.edu.utn.dds.k3003.Controller.TrasladoController;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class WebApp {

    private static EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {

        var objectMapper = createObjectMapper();
        startEntityManagerFactory();
        var fachada = new Fachada(entityManagerFactory);

        fachada.setViandasProxy(new ar.edu.utn.dds.k3003.clients.ViandasProxy(objectMapper));
        fachada.setHeladerasProxy(new ar.edu.utn.dds.k3003.clients.HeladerasProxy(objectMapper));
        fachada.setColaboradoresProxy(new ar.edu.utn.dds.k3003.clients.ColaboradorProxy(objectMapper));

        var port = Integer.parseInt(System.getenv("PORTLOGISTICA"));

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
        app.get("/healthcheck", WebApp::healthcheck);

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

    private static void healthcheck(Context context){
        try {
            Process process = Runtime.getRuntime().exec("pgrep java");
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                context.status(HttpStatus.OK);
            } else {
                context.status(HttpStatus.SERVICE_UNAVAILABLE);
            }
        } catch (Exception e) {
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static void startEntityManagerFactory() {
        Map<String, Object> configOverrides = new HashMap<>();
        configOverrides.put("javax.persistence.jdbc.url", System.getenv("jdbcUrl"));
        configOverrides.put("javax.persistence.jdbc.user", System.getenv("jdbcUser"));
        configOverrides.put("javax.persistence.jdbc.password", System.getenv("jdbcPassword"));
        configOverrides.put("javax.persistence.jdbc.driver", System.getenv("jdbcDriver"));
        configOverrides.put("hibernate.hbm2ddl.auto", System.getenv("hibernateDDL"));
        configOverrides.put("hibernate.connection.pool_size", System.getenv("hibernatePoolSize"));
        configOverrides.put("hibernate.dialect", System.getenv("hibernateDialect"));
        configOverrides.put("hibernate.connection.release_mode", System.getenv("hibernateConnectionMode"));
        configOverrides.put("hibernate.archive.autodetection", System.getenv("hibernateArchiveDetection"));
        configOverrides.put("hibernate.default_schema", System.getenv("hibernateSchema"));
        configOverrides.put("hibernate.format_sql", System.getenv("hibernateFormatSql"));

        entityManagerFactory = Persistence.createEntityManagerFactory("db", configOverrides);
    }
}