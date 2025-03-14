package ar.edu.utn.dds.k3003.Service;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UtilsMetrics {
    public static void enviarNuevoTrasladoRealizado() {
        Dotenv dotenv = Dotenv.load();
        var url = dotenv.get("URL_METRICS_1");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (!(response.statusCode() == 200)) {
                        throw new RuntimeException("Error en la respuesta: " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    throw new RuntimeException("Error durante la llamada a la API", e);
                });
    }

    public static void actualizarTrasladosEnCurso(Long trasladoId, boolean aumentar) {
        Dotenv dotenv = Dotenv.load();
        var url = dotenv.get("URL_METRICS_2");

        if (aumentar) {
            url = url + "/incrementar";
        } else {
            url = url + "/disminuir";
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (!(response.statusCode() == 200)) {
                        throw new RuntimeException("Error en la respuesta: " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    throw new RuntimeException("Error durante la llamada a la API", e);
                });
    }

}
