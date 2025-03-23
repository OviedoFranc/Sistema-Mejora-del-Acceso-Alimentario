package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.controller.MetricController;
import ar.edu.utn.dds.k3003.model.Constants;
import ar.edu.utn.dds.k3003.service.DDMetricsUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.micrometer.MicrometerPlugin;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WebApp {

    public static void main(String[] args) {

        var port = Integer.parseInt(System.getenv("PORTMETRICS"));

        final var metricsUtils = new DDMetricsUtils("transferencias");
        final var registry = metricsUtils.getRegistry();

        // Configuración de Micrometer
        MicrometerPlugin micrometerPlugin = new MicrometerPlugin(micrometerPluginConfig -> micrometerPluginConfig.registry = registry);
        final var metricController = new MetricController(metricsUtils);

        Javalin app = Javalin.create(config -> config.registerPlugin(micrometerPlugin)).start(port);

        app.get("/healthcheck",WebApp::healthcheck);
        app.get("/metrics/aperturaHeladera", metricController::aperturaHeladera);
        app.get("/metrics/fallaHeladeras", metricController::fallaHeladeras);
        app.get("/metrics/incidentes/{accion}", metricController::incidentesActivos);
        app.get("/metrics/trasladosRealizados", metricController::trasladosRealizados);
        app.get("/metrics/trasladosEnCurso/{accion}", metricController::trasladosEnCurso);
        app.get("/metrics/cantColaboradores/{accion}", metricController::CantColaboradores);
        app.get("/metrics/cantDonadores/{accion}", metricController::cantDonadores);
        app.get("/metrics/cantTransportadores/{accion}", metricController::cantTransportadores);
        app.get("/metrics/viandasCreadas/{accion}", metricController::viandasCreadas);
        app.get("/metrics/viandasEnTransporte/{accion}", metricController::viandasEnTransporte);
        app.get("/metrics/viandasVencidas/{accion}", metricController::viandasVencidas);
        app.delete("/metrics/clear", metricController::resetearMetricas);
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

}
