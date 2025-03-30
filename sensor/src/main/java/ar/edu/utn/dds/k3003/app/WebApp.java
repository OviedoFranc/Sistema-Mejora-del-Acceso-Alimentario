package ar.edu.utn.dds.k3003.app;

import io.javalin.Javalin;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.javalin.http.HttpStatus;

public class WebApp {
    public static Channel channel;
    public static Javalin app;
    private static String QUEUE_NAME;
    private static Integer tiempoSeteoNuevasTemperaturas;
    private static Set<Integer> heladerasID = new HashSet<>();

    public static void main(String[] args) throws IOException, TimeoutException {
        QUEUE_NAME = System.getenv("QUEUE_NAME");
        tiempoSeteoNuevasTemperaturas =  Integer.parseInt(System.getenv("TIMECRON_NEW_TEMPERATURES"));
        channel = initialCloudAMQPTopicConfiguration();
        app = Javalin.create();
        app.start(Integer.parseInt(System.getenv("PORTSENSOR")));
        app.get("/addSensorHeladera/{Id}", ctx -> { Integer id = Integer.parseInt(ctx.pathParam("Id")); heladerasID.add(id); ctx.status(HttpStatus.OK); } );
        app.get("/stopSensorHeladera/{Id}", ctx -> { Integer id = Integer.parseInt(ctx.pathParam("Id")); heladerasID.remove(id); ctx.status(HttpStatus.OK); } );
        cronSensor();
    }
    private static void pushMessageQueue() throws IOException {
        Random rand = new Random();
        for (Integer heladeraID: heladerasID) {
            String mensaje = String.format("Heladera %d - Temperatura %d°C",
                    heladeraID,
                    rand.nextInt(15));
            Channel channel = WebApp.channel;
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish(mensaje, QUEUE_NAME, null, mensaje.getBytes("UTF-8"));
        }
    }
    private static Channel initialCloudAMQPTopicConfiguration() throws IOException, TimeoutException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv("QUEUE_HOST"));
        factory.setUsername(System.getenv("QUEUE_USERNAME"));
        factory.setPassword(System.getenv("QUEUE_PASSWORD"));
        factory.setVirtualHost(System.getenv("VHOST"));
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }
    private static void cronSensor() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable tarea = () -> {
            try {
                pushMessageQueue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        scheduler.scheduleAtFixedRate(tarea, 0, tiempoSeteoNuevasTemperaturas, TimeUnit.SECONDS);
    }
}
