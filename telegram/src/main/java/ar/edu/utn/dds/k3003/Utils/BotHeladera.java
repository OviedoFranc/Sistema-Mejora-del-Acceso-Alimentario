package ar.edu.utn.dds.k3003.Utils;


import ar.edu.utn.dds.k3003.model.IncidenteDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.edu.utn.dds.k3003.model.RetiroDTODay;
import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class BotHeladera {

  Dotenv dotenv = Dotenv.load();
  String url = /*"https://heladeras-prueba.onrender.com"*/ dotenv.get("URL_HELADERA");

  public void verIncidentesDeHeladera(Long chatId, String mensaje, Comandos comandos){
	  String[] partes = mensaje.split("\\s+");
	  int heladeraId = Integer.parseInt(partes[0]);
    try {
      String uri = url + "/heladeras/" + heladeraId + "/obtenerHistorialIncidentes";

      // Crear la solicitud GET
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .header("Content-Type", "application/json")
          .GET()
          .build();

      HttpClient client = HttpClient.newHttpClient();
      // Enviar la solicitud y obtener la respuesta
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        System.out.println("Historial de incidentes: " + response.body());
        List<IncidenteDTO> incidenteDTOS = parseIncidenteDTO(response.body());

          if (incidenteDTOS == null || incidenteDTOS.isEmpty()) {
              String mssge = "No se han registrado incidentes para esta heladera.";
              comandos.sendMessage(chatId, mssge);
              System.out.println(mssge);
              return;
          }

          StringBuilder mssge = new StringBuilder("Historial de incidentes para la Heladera " + heladeraId + ":\n");
          for (IncidenteDTO incidente : incidenteDTOS) {
              mssge.append(String.format("Tipo Incidente: %s, Fecha de Incidente: %s\n",
                      incidente.getTipoIncidente(), incidente.getFechaIncidente()));
          }

          comandos.sendMessage(chatId, mssge.toString());
          System.out.println(mssge.toString());


      } else {
        System.out.println("Error al obtener el historial de incidentes: Codigo de estado" + response.statusCode() + " - " + response.body());
        comandos.sendMessage(chatId, "Error al obtener el historial de incidentes");
      }
    } catch (Exception e) {
      e.printStackTrace();
      comandos.sendMessage(chatId, "Ocurrió un error al obtener el historial de incidentes");
      System.out.println("Ocurrió un error al obtener el historial de incidentes: " + e.getMessage());
    }
  }
  
  public void retirarVianda(Long chatId, String mensaje, Comandos comandos) {
  	String[] partes = mensaje.split("\\s+");
  	
  	String qrVianda = partes[0];
  	int heladeraId = Integer.parseInt(partes[1]);
      
      try {
          String requestBody = String.format(
                  "{\"qrVianda\": \"%s\", \"heladeraId\": %d}",
                  qrVianda, heladeraId
          );

          HttpRequest request = HttpRequest.newBuilder()
                  .uri(URI.create(url + "/retiros"))
                  .header("Content-Type", "application/json")
                  .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                  .build();

          HttpClient client = HttpClient.newHttpClient();

          HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
          
          switch(response.statusCode()) {
          case 404:
          	comandos.sendMessage(chatId, "No se encontro la heladera");
              System.out.println("No se encontro la heladera: " + response.statusCode() + " - " + response.body());
          	break;
          case 403:
          	comandos.sendMessage(chatId, "La heladera no esta habilitada");
              System.out.println("La heladera no esta habilitada: " + response.statusCode() + " - " + response.body());
          	break;
          case 200:
          	comandos.sendMessage(chatId, "Se retiro la vianda exitosamente");
              System.out.println("Se retiro la vianda exitosamente: " + response.body());
              break;
          default:
          	comandos.sendMessage(chatId, "Hubo un error en la solicitud");
              System.out.println("Hubo un error en la solicitud: " + response.statusCode() + " - " + response.body());
          	break;
          }
      } catch (Exception e) {
          e.printStackTrace();
          comandos.sendMessage(chatId,"Ocurrió un error al retirar la vianda");
          System.out.println("Ocurrió un error al retirar la vianda: " + e.getMessage());
      }
  }

  public void verOcupacion(Long chatId, String mensaje, Comandos comandos){
	String[] partes = mensaje.split("\\s+");
    int heladeraId = Integer.parseInt(partes[0]);
    try {
      String uri = url + "/heladeras/" + heladeraId + "/cantidadViandasHastaLLenar";

      // Crear la solicitud GET
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .header("Content-Type", "application/json")
          .GET()
          .build();

      HttpClient client = HttpClient.newHttpClient();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {

        List<Integer> viandasInfo = parseViandasInfo(response.body());

        if (viandasInfo != null && viandasInfo.size() == 2) {
          Integer cantidadHastaLlenar = viandasInfo.get(0);
          Integer cantidadMaxima = viandasInfo.get(1);

          // Calcular el porcentaje de ocupación
          double porcentajeLlenado = (double) (cantidadMaxima - cantidadHastaLlenar) / cantidadMaxima * 100;

          String mssge = String.format(
              "La heladera está al %.2f%% de su capacidad.\n" +
                  "Viandas disponibles hasta llenar: %d\n" +
                  "Capacidad máxima de viandas: %d",
              porcentajeLlenado, cantidadHastaLlenar, cantidadMaxima);

          System.out.println(mssge);
          comandos.sendMessage(chatId, mssge);

        } else {
          System.out.println("Error: La respuesta no tiene los datos esperados.");
          comandos.sendMessage(chatId, "Error al obtener la ocupación de viandas.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      comandos.sendMessage(chatId, "Ocurrió un error al obtener la ocupación de viandas.");
      System.out.println("Ocurrió un error al obtener la ocupación de viandas: " + e.getMessage());
    }
  }

  public void verRetirosDelDia(Long chatId, String mensaje, Comandos comandos){
	  String[] partes = mensaje.split("\\s+");
	  int heladeraId = Integer.parseInt(partes[0]);
    try {
      String uri = url + "/heladeras/" + heladeraId + "/obtenerRetirosDelDia";

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .header("Content-Type", "application/json")
          .GET()
          .build();

      HttpClient client = HttpClient.newHttpClient();
      // Espera un ida y vuelta
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        // Parsear la respuesta, asumiendo que la respuesta es una lista de objetos RetiroDTODay en formato JSON
        List<RetiroDTODay> retirosDelDia = parseRetirosDelDia(response.body());
        System.out.println("Lista de retiros del dia" + response.body());

        // Si la lista está vacía, se notifica
        if (retirosDelDia == null || retirosDelDia.isEmpty()) {
          String mssge = "No se han registrado retiros para esta heladera.";
          comandos.sendMessage(chatId, mssge);
          System.out.println(mssge);
          return;
        }

        StringBuilder mssge = new StringBuilder("Retiros del día para la heladera " + heladeraId + ":\n");
        for (RetiroDTODay retiro : retirosDelDia) {
          mssge.append(String.format("QR Vianda: %s, Tarjeta: %s, Fecha de Retiro: %s\n",
              retiro.getQrVianda(), retiro.getTarjeta(), retiro.getFechaRetiro()));
        }

        comandos.sendMessage(chatId, mssge.toString());
        System.out.println(mssge.toString());

      } else {
        String errorMessage = "Error al obtener los retiros del día. Código de estado: " + response.statusCode();
        comandos.sendMessage(chatId, errorMessage);
        System.out.println(errorMessage);
      }

    } catch (Exception e) {
      e.printStackTrace();
      comandos.sendMessage(chatId, "Ocurrió un error al obtener los retiros del día.");
      System.out.println("Ocurrió un error al obtener los retiros del día: " + e.getMessage());
    }
  }

  private List<RetiroDTODay> parseRetirosDelDia(String jsonResponse) {
    ObjectMapper objectMapper = new ObjectMapper();
    //objectMapper.registerModule(new JavaTimeModule());
    try {
    	List<RetiroDTODay> lista = objectMapper.readValue(jsonResponse, new TypeReference<List<RetiroDTODay>>(){});
    	return lista;
    } catch (IOException e) {
    	e.printStackTrace();
    	return null;
    }
  }

  private List<IncidenteDTO> parseIncidenteDTO(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
      try {
          // Parse the JSON array into a List of Lists (Object) first
          List<List<Object>> list = objectMapper.readValue(jsonResponse, new TypeReference<List<List<Object>>>(){});
          List<IncidenteDTO> incidenteList = new ArrayList<>();

          for (List<Object> incident : list) {
              Integer incidenteId = (Integer) incident.get(0);
              String fechaIncidente = (String) incident.get(5);
              String tipoIncidente = (String) incident.get(6);
              incidenteList.add(new IncidenteDTO(tipoIncidente, incidenteId, fechaIncidente));
          }
          return incidenteList;

      } catch (IOException e) {
          e.printStackTrace();
          return null;
      }
    }


  public static List<Integer> parseViandasInfo(String responseBody) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      // Convertimos la respuesta en una lista de enteros
      return objectMapper.readValue(responseBody, List.class);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public void eliminarSuscripcion(Long chatId, String mensaje, Comandos comandos) {
  	String[] partes = mensaje.split("\\s+");
  	
  	String tipoDeSuscripcion = partes[0];
  	int heladeraId = Integer.parseInt(partes[1]);
  	int colaboradorId = Integer.parseInt(partes[2]);
      
      try {
          String uri = String.format("/%d/suscripciones",
          		heladeraId
          );
          String requestBody = String.format(
                  "{\"codigoQR\": \"%s\", \"heladeraId\": %d, \"colaboradorId\": %d}",
                  tipoDeSuscripcion, heladeraId, colaboradorId
          );

          HttpRequest request = HttpRequest.newBuilder()
                  .uri(URI.create(url + uri))
                  .header("Content-Type", "application/json")
                  .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                  .build();

          HttpClient client = HttpClient.newHttpClient();

          HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
          
          switch(response.statusCode()) {
          case 404:
          	comandos.sendMessage(chatId, "No se encontro la heladera");
              System.out.println("No se encontro la heladera: " + response.statusCode() + " - " + response.body());
          	break;
          case 400:
          	comandos.sendMessage(chatId, "El tipo de suscripcion es invalido o falta tipo de suscripcion, id de heladera o id del colaborador");
              System.out.println("El tipo de suscripcion es invalido o falta tipo de suscripcion, id de heladera o id del colaborador: " + response.statusCode() + " - " + response.body());
          	break;
          case 201:
          	comandos.sendMessage(chatId, "Se elimino excitosamente la suscripcion");
              System.out.println("Se elimino excitosamente la suscripcion: " + response.body());
              break;
          default:
          	comandos.sendMessage(chatId, "Hubo un error en la solicitud");
              System.out.println("Hubo un error en la solicitud: " + response.statusCode() + " - " + response.body());
          	break;
          }
      } catch (Exception e) {
          e.printStackTrace();
          comandos.sendMessage(chatId,"Ocurrió un error al intentar de desuscribirse");
          System.out.println("Ocurrió un error al intentar de desuscribirse: " + e.getMessage());
      }
  }
}

