package ar.edu.utn.dds.k3003.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UtilsMetrics {
    public static void actualizarColaboradores(boolean aumentar, boolean donador) {
        var url = System.getenv("URL_METRICS" + "/cant");
        HttpClient client = HttpClient.newHttpClient();

        if (donador) {
            url = url + "Donadores";
        } else {
            url = url + "Transportadores";
        }

        if (aumentar) {
            url = url + "/incrementar";
        } else {
            url = url + "/disminuir";
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

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

    public static void actualizarCantColaboradores(boolean aumentar) {
        var url = System.getenv("URL_METRICS" + "/cantColaboradores");
        HttpClient client = HttpClient.newHttpClient();

        if (aumentar) {
            url = url + "/incrementar";
        } else {
            url = url + "/disminuir";
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

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