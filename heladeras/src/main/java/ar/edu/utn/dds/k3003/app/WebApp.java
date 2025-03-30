package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.Service.IncidenteService;
import ar.edu.utn.dds.k3003.controller.HeladeraController;
import ar.edu.utn.dds.k3003.clients.ViandasProxy;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.utils.utils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

public class WebApp {

    public static EntityManagerFactory entityManagerFactory;
    public static Channel channel;
    public static Javalin app;
    public static IncidenteService incidenteService;
    private static List<Integer> heladerasExcluidasDeSeteoTemperatura = new ArrayList<>();
    private static Integer tiempoSeteoNuevasTemperaturas;
    private static Integer tiempoRevisarUltimaTemperaturaSeteada;


    public static void main(String[] args) throws IOException, TimeoutException {

        startEntityManagerFactory();
        var objectMapper = createObjectMapper();
        Fachada fachada = new Fachada(entityManagerFactory);
        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        var port = Integer.parseInt(System.getenv("PORTHELADERA"));
        tiempoSeteoNuevasTemperaturas =  Integer.parseInt(System.getenv("TIMECRON_NEW_TEMPERATURES"));
        tiempoRevisarUltimaTemperaturaSeteada =  Integer.parseInt(System.getenv("TIMECRON_REVIEW_LAST_TEMPERATURE"));

        app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                configureObjectMapper(mapper);
            }));
        }).start(port);

        incidenteService = new IncidenteService(entityManagerFactory, fachada);
        var heladeraController = new HeladeraController(fachada,incidenteService);

        app.get("/heladeras/crearGenericas", heladeraController::crearHeladerasGenericas);
        app.get("/heladeras/deleteAll", heladeraController::borrarTodo);
        app.post("/heladeras", heladeraController::agregar);
        app.get("/heladeras/{heladeraId}", heladeraController::obtenerHeladera);
        app.post("/depositos/{heladeraId}/{qrVianda}", heladeraController::depositarVianda);
        app.post("/retiros", heladeraController::retirarVianda);
        app.post("/temperaturasEnCola/registrar", heladeraController::registrarTemperaturaEnCola);
        app.get("/heladeras/{heladeraId}/temperaturas", heladeraController::obtenerTemperaturas);
        app.post("/suscripciones", heladeraController::registrarSuscripcion);
        app.get("/heladeras/{heladeraId}/reportarFalla", heladeraController::reportarFalla);
        app.get("/heladeras/{heladeraId}/arreglarFalla", heladeraController::arreglarFalla);
        app.get("/heladeras/{heladeraId}/viandasEnHeladera", heladeraController::viandasEnHeladera);
        app.get("/heladeras/{heladeraId}/obtenerHistorialIncidentes", heladeraController::obtenerHistorialIncidentes);
        app.get("/heladeras/{heladeraId}/obtenerRetirosDelDia", heladeraController::obtenerRetirosDelDia);
        app.get("/heladeras/{heladeraId}/cantidadViandasHastaLLenar", heladeraController::cantidadViandasHastaLLenar);
        app.delete("/{heladeraId}/suscripciones", heladeraController::eliminarSuscripcion);
        app.get("/{heladeraId}/suscripciones", heladeraController::obtenerSuscripciones);
        channel = initialCloudAMQPTopicConfiguration();
        setupConsumerTemperatura(heladeraController);
        setupConsumerMovimiento();
        cronRevisadorUltimaTemperaturaSeteadaEnHeladeras();
        reporteContinuoTemperaturaCola(heladeraController);
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

    private static Channel initialCloudAMQPTopicConfiguration() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv("QUEUE_HOST"));
        factory.setUsername(System.getenv("QUEUE_USERNAME"));
        factory.setPassword(System.getenv("QUEUE_PASSWORD"));
        factory.setVirtualHost(System.getenv("VHOST"));
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }

    private static void setupConsumerTemperatura(HeladeraController heladeraController) throws IOException {
        String QUEUE = System.getenv("QUEUE_NAME_TEMPERATURA");
        channel.queueDeclare(QUEUE, true, false, false, null);
        System.out.println("Esperando mensajes en la cola " + QUEUE);

        // PROCESAMIENTO DE LOS MENSAJES
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            String[] parts = message.split(" - ");
            if (parts.length == 2) {
                String heladeraPart = parts[0]; // "Heladera X"
                String temperaturaPart = parts[1]; // "Temperatura Y°C"

                // Extraigo los valores de los mensajes
                int heladeraId = Integer.parseInt(heladeraPart.split(" ")[1]);
                Integer temperatura = Integer.parseInt(temperaturaPart.split(" ")[1].replace("°C", ""));

                TemperaturaDTO temperaturaDTO = new TemperaturaDTO(temperatura, heladeraId, LocalDateTime.now());

                System.out.println("Processed TemperaturaDTO: " + temperaturaDTO);
                heladeraController.registrarTemperatura(temperaturaDTO);
            } else {
                System.err.println("Formato de mensaje incorrecto: " + message);
            }

        };
        channel.basicConsume(QUEUE, true, deliverCallback, consumerTag -> { });
    }

    private static void setupConsumerMovimiento() throws IOException {

        String QUEUE = System.getenv("QUEUE_NAME_MOVIMIENTO");
        channel.queueDeclare(QUEUE, false, false, false, null);
        System.out.println("Esperando mensajes en la cola " + QUEUE);

        // PROCESAMIENTO DE LOS MENSAJES
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            // Se espera un formato: "Movimiento Heladera ID"
            String[] parts = message.split(" - ");
            if (parts.length == 1) {
                String movimientoPart = parts[0]; // "Movimiento Heladera ID"

                // Extraigo el ID del movimiento
                int heladeraID = Integer.parseInt(movimientoPart.split(" ")[2]); // Asumiendo "Movimiento X"

                System.out.println("Movimiento en Heladera " + heladeraID);
                heladerasExcluidasDeSeteoTemperatura.add(heladeraID);
                incidenteService.movimientoHeladera(heladeraID);
            } else {
                System.err.println("Formato de mensaje incorrecto: " + message);
            }
        };

        channel.basicConsume(QUEUE, true, deliverCallback, consumerTag -> {});
    }

    private static void cronRevisadorUltimaTemperaturaSeteadaEnHeladeras() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Tarea a ejecutar
        Runnable tarea = () -> {
            incidenteService.controlarTiempoDeEsperaMaximoTemperaturas();
        };
        scheduler.scheduleAtFixedRate(tarea, 0, tiempoRevisarUltimaTemperaturaSeteada, TimeUnit.SECONDS);
    }

    private static void cronReseteadorDeRetirosDelDia(Fachada fachada) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        // Hora de ejecución deseada (00:00:00)
        int HORA_EJECUCION = 0;
        int MINUTO_EJECUCION = 0;
        int SEGUNDO_EJECUCION = 0;
        LocalTime ahora = LocalTime.now();
        LocalTime horaEjecucion = LocalTime.of(HORA_EJECUCION, MINUTO_EJECUCION, SEGUNDO_EJECUCION);
        if (ahora.isAfter(horaEjecucion)) {
            horaEjecucion = horaEjecucion.plusHours(24);
        }
        long tiempoHastaProximaEjecucion = ChronoUnit.SECONDS.between(ahora, horaEjecucion);

        Runnable tarea = () -> {
            fachada.limpiarRetirosDelDia();
        };

        // Programar la tarea para que se ejecute a la hora calculada y luego cada 24 horas
        scheduler.scheduleAtFixedRate(tarea, tiempoHastaProximaEjecucion, 24, TimeUnit.HOURS);
    }

    private static void reporteContinuoTemperaturaCola(HeladeraController heladeraController) throws IOException {
        String QUEUE = System.getenv("QUEUE_NAME_TEMPERATURA");
        channel.queueDeclare(QUEUE, false, false, false, null);
        System.out.println("Esperando TEMPERATURAS en la cola " + QUEUE);

        // PROCESAMIENTO DE LOS MENSAJES
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // Mensaje esperado "Heladera %d - Temperatura %d°C",
            String[] parts = message.split(" - ");
            if (parts.length == 1) {
                String heladera = parts[1]; // "Heladera ID"
                // Extraigo el ID de la heladera
                int heladeraID = Integer.parseInt(heladera.split(" ")[2]); //
                String temp = parts[1]; // "Temperatura temp"
                // Extraigo la Temperatura de la heladera
                int temperatura = Integer.parseInt(temp.split(" ")[2]);
                System.out.println("Heladera " + heladeraID + " - Temperatura "+ temperatura + "°C");
                TemperaturaDTO temperaturaDTO = new TemperaturaDTO(temperatura, heladeraID, LocalDateTime.now());
                heladeraController.registrarTemperatura(temperaturaDTO);
            } else {
                System.err.println("Formato de mensaje incorrecto: " + message);
            }
        };

        channel.basicConsume(QUEUE, true, deliverCallback, consumerTag -> {});
    }
}
