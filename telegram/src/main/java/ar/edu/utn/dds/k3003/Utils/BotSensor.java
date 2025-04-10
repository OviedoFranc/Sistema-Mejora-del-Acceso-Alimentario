package ar.edu.utn.dds.k3003.Utils;

import ar.edu.utn.dds.k3003.model.IncidenteDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class BotSensor {
    String url = System.getenv("URL_SENSOR");

    public void pararSensor(Long chatId, String mensaje, Comandos comandos){
        String[] partes = mensaje.split("\\s+");
        int heladeraId = Integer.parseInt(partes[0]);
        try {
            String uri = url + "/stopSensorHeladera/" + heladeraId;

            // Crear la solicitud GET
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                    String mssge = "Exito al parar el sensor";
                    comandos.sendMessage(chatId, mssge);
            } else {
                String mssge = "Error al parar el sensor";
                comandos.sendMessage(chatId, mssge);
            }
        }
        catch (Exception e){
        e.printStackTrace();
        }
    }
    public void reanudarSensor(Long chatId, String mensaje, Comandos comandos){
        String[] partes = mensaje.split("\\s+");
        int heladeraId = Integer.parseInt(partes[0]);
        try {
            String uri = url + "/addSensorHeladera/" + heladeraId;

            // Crear la solicitud GET
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String mssge = "Exito al reanudar el sensor";
                comandos.sendMessage(chatId, mssge);
            } else {
                String mssge = "Error al reanudar el sensor";
                comandos.sendMessage(chatId, mssge);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}