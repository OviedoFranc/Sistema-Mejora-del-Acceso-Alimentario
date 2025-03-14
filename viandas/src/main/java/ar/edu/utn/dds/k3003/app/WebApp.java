package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladerasProxy;
import ar.edu.utn.dds.k3003.controller.ViandaController;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WebApp {

  public static void main(String[] args) {

    var env = System.getenv();
    var objectMapper = createObjectMapper();
    var fachada = new Fachada();
    fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));
    var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));
    var viandasController = new ViandaController(fachada);
    var app = Javalin.create(config -> {
        config.jsonMapper(new JavalinJackson().updateMapper(WebApp::configureObjectMapper));
    }).start(port);

    //Error de Servidor: Could not find /.env on the classpath
    
    app.post("/viandas", viandasController::agregar);
    app.post("/viandasDepositar", viandasController::agregarYDepositar);
    app.post("/viandasGenericas", viandasController::agregarGenericas);
    app.post("/viandasGenericasDepositar", viandasController::agregarGenericasYDepositarlas);
    app.get("/viandas/search/findByColaboradorId", viandasController::obtenerXColIDAndAnioAndMes);
    app.get("/viandas/{qr}", viandasController::obtenerXQR);
    app.get("/viandas/{qr}/vencida", viandasController::evaluarVencimiento);
    app.patch("/viandas/{qrVianda}", viandasController::modificarHeladeraXQR);
    app.patch("/viandas/{qr}/estado", viandasController::modificarEstadoVianda);
    app.delete("/clear", viandasController::limpiarDB);

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
