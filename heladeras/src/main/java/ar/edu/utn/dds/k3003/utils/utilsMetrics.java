package ar.edu.utn.dds.k3003.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class utilsMetrics {
  public static void enviarNuevaAperuraDeHeladera() {
    var url = System.getenv("URL_METRICS");
    url = url +"/aperturaHeladera";
    HttpClient client = HttpClient.newHttpClient();

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

  public static void metricaIncidenteHeladera(Boolean activo) {
    var url = System.getenv("URL_METRICS");
    url = url +"/incidentes";
    HttpClient client = HttpClient.newHttpClient();
      if (activo) {
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

  public static void fallaHeladeras() {
    var url = System.getenv("URL_METRICS");
    url = url +"/fallaHeladeras";
    HttpClient client = HttpClient.newHttpClient();

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
