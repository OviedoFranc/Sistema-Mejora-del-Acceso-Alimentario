package ar.edu.utn.dds.k3003.Utils;

import ar.edu.utn.dds.k3003.app.WebApp.ChatIdRegistry;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;
import org.json.JSONObject;

public class BotLogistica {

    String urlLogistica = System.getenv("URL_LOGISTICA");

    public void darDeAltaRuta(Long chatId, String mensaje, Comandos comandos) {

        String[] partes = mensaje.split("\\s+");

        int colaboradorId = Integer.parseInt(partes[0]);
        int heladeraIdOrigen = Integer.parseInt(partes[1]);
        int heladeraIdDestino = Integer.parseInt(partes[2]);

        try {
            String requestBody = String.format(
                    "{\"colaboradorId\": %d, \"heladeraIdOrigen\": %d, \"heladeraIdDestino\": %d}",
                    colaboradorId, heladeraIdOrigen, heladeraIdDestino
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlLogistica + "/rutas"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                comandos.sendMessage(chatId, "Ruta creada exitosamente desde la heladera " + heladeraIdOrigen + " hasta la heladera " + heladeraIdDestino);
                System.out.println("Ruta creada exitosamente: " + response.body());
            } else {
                comandos.sendMessage(chatId,"Error al crear la ruta");
                System.out.println("Error al crear la ruta: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            comandos.sendMessage(chatId,"Ocurrió un error al crear la ruta");
            System.out.println("Ocurrió un error al crear la ruta: " + e.getMessage());
        }
    }


    public void asignarTraslado(Long chatId, String mensaje, Comandos comandos){
        String[] partes = mensaje.split("\\s+");

        String listQrViandas = partes[0];
        String status = "CREADO";
        String fechaTraslado = "2024-05-15T21:10:40Z";
        int heladeraOrigen = Integer.parseInt(partes[1]);
        int heladeraDestino = Integer.parseInt(partes[2]);

        try {
            String requestBody = String.format(
                    "{\"listQrViandas\": [\"%s\"], \"status\": \"%s\", \"fechaTraslado\": \"%s\", \"heladeraOrigen\": %d, \"heladeraDestino\": %d}",
                    listQrViandas, status, fechaTraslado, heladeraOrigen, heladeraDestino
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlLogistica + "/traslados"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //aca busca el colaboradorId de la response
            System.out.println(response.body());
            JSONObject jsonResponse = new JSONObject(response.body());
            int colaboradorIdTransportista = jsonResponse.getInt("colaboradorId");
            Long idTraslado = jsonResponse.getLong("id");

            Long chatIdTransportista = ChatIdRegistry.obtenerChatId(colaboradorIdTransportista);

            if (response.statusCode() == 200) {
                comandos.sendMessage(chatIdTransportista, "Traslado asignado exitosamente " + "ID: " + idTraslado);
                System.out.println("Traslado asignado exitosamente: " + response.body());
            } else {
                comandos.sendMessage(chatId,"Error al asignar el traslado");
                System.out.println("Error al asignar el traslado: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            comandos.sendMessage(chatId,"Ocurrió un error al asignar el traslado");
            System.out.println("Ocurrió un error al asignar el traslado: " + e.getMessage());
        }
    }

    public void iniciarFinalizarTraslado(Long chatId, String mensaje, Comandos comandos){
        String[] partes = mensaje.split("\\s+");

        System.out.println("entro a iniciar finalizar traslado");

        String status = partes[0];
        int idTraslado = Integer.parseInt(partes[1]);

        try {
            String requestBody = String.format(
                    "{\"status\": \"%s\"}",
                    status
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlLogistica + "/traslados/" + idTraslado))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody)) // Método PATCH
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
            	String mensajeTraslado;
            	if(status.equals("EN_VIAJE")) {
            		mensajeTraslado = "iniciado";
            	} else {
            		mensajeTraslado = "finalizado";
            	}
                comandos.sendMessage(chatId, "Traslado " + mensajeTraslado + " exitosamente");
                System.out.println("Traslado " + mensajeTraslado + " exitosamente: " + response.body());
            } else {
                comandos.sendMessage(chatId,"Error al asignar el trasladoa");
                System.out.println("Error al iniciar el translado: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            comandos.sendMessage(chatId,"Ocurrió un error al asignar el translado");
            System.out.println("Ocurrió un error al iniciar el translado: " + e.getMessage());
        }
    }


    
}