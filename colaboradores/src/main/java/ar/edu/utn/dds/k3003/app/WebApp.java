package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladeraProxy;
import ar.edu.utn.dds.k3003.clients.LogisticaProxy;
import ar.edu.utn.dds.k3003.clients.ViandasProxy;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import ar.edu.utn.dds.k3003.model.Colaborador;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.utn.dds.k3003.controller.ColaboradorController;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.*;

public class WebApp {

    public static EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {
        var env = System.getenv();

        ObjectMapper objectMapper = createObjectMapper();
        startEntityManagerFactory();
        var fachada = new Fachada(entityManagerFactory);

        var port = Integer.parseInt(env.getOrDefault("PORTCOLABORADOR", "8085"));

        fachada.setLogisticaProxy(new LogisticaProxy(objectMapper));
        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setHeladerasProxy(new HeladeraProxy(objectMapper));

        var colaboradorController = new ColaboradorController(fachada);

        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(WebApp::configureObjectMapper));
        }).start(port);

        app.get("/healthcheck", WebApp::healthcheck);
        app.post("/colaboradores", colaboradorController::agregar);
        app.post("/colaboradores/avisar",colaboradorController::avisar);
        app.post("/colaboradores/suscribirse", colaboradorController::suscribirse);
        app.get("/colaboradores/{id}", colaboradorController::obtenerColaborador);
        app.post("/colaboradores/{id}", colaboradorController::modificarColaboracion);
        app.get("/colaboradores/{id}/puntos", colaboradorController::obtenerPuntos);
        app.post("/colaboradores/{id}/donarPesos/{cantPesos}", colaboradorController::donarPesos);
        app.post("/colaboradores/{heladeraId}/reportar", colaboradorController::reportarHeladera);
        app.post("/colaboradores/{id}/reparar/{heladeraId}", colaboradorController::repararHeladera);
        app.get("/colaboradores/{id}/formas",colaboradorController::obtenerFormas);
        app.put("/formula", colaboradorController::actualizarPesosPuntos);
        app.delete("/clear",colaboradorController::clean);
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
