package ar.edu.utn.dds.k3003.Utils;

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

public class BotVianda {

    String urlViandas= System.getenv("URL_VIANDA");

    public void crearYDepositarVianda(Long chatId, String mensaje, Comandos comandos) {
    	String[] partes = mensaje.split("\\s+");
    	
    	String codigoQR = partes[0];
    	String fechaElaboracion = partes[1];
    	int colaboradorId = Integer.parseInt(partes[2]);
        int heladeraId = Integer.parseInt(partes[3]);
        
        try {
            String requestBody = String.format(
                    "{\"codigoQR\": \"%s\", \"fechaElaboracion\": \"%s\", \"colaboradorId\": %d, \"heladeraId\": %d}",
                    codigoQR, fechaElaboracion, colaboradorId, heladeraId
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlViandas + "/viandasDepositar"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                comandos.sendMessage(chatId, "Vianda creada y depositada exitosamente");
                System.out.println("Vianda creada y depositada exitosamente: " + response.body());
            } else {
                comandos.sendMessage(chatId,"Error al crear o depositar la Vianda");
                System.out.println("Error al crear o depositar la Vianda: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            comandos.sendMessage(chatId,"Ocurrió un error al crear y depositar la vianda");
            System.out.println("Ocurrió un error al crear y depositar la vianda: " + e.getMessage());
        }
    }
    
}


