package ar.edu.utn.dds.k3003.Utils;

import ar.edu.utn.dds.k3003.app.BotApp;
import ar.edu.utn.dds.k3003.model.DatosColaboradorDTO;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;
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

public class BotColaborador {

    Dotenv dotenv = Dotenv.load();
    String url = /*"https://colaboradores-prueba.onrender.com"*/ dotenv.get("URL_COLABORADOR");
    
    public void agregarColaborador(Long chatId, String mensaje, Comandos comandos) {
        String[] partes = mensaje.split("\\s+");

        String nombre = partes[0];
        String formas = partes[1];
        try {
            formas = formas.concat("\",").concat("\"").concat(partes[2]);
            formas = formas.concat("\",").concat("\"").concat(partes[3]);
        }
        catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            String requestBody = String.format(
                    "{\"nombre\": \"%s\", \"formas\": [\"%s\"]}",
                    nombre,formas
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/colaboradores"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());
            int colaboradorId = jsonResponse.getInt("id");

            if (response.statusCode() == 201) {
                comandos.sendMessage(chatId, "Colaborador " + colaboradorId + " creado exitosamente");
                System.out.println("Colaborador creado exitosamente: " + response.body());
            } else {
                comandos.sendMessage(chatId,"Error al crear colaborador");
                System.out.println("Error al crear colaborador: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            comandos.sendMessage(chatId,"Ocurrió un error al crear el colaborador");
            System.out.println("Ocurrió un error al crear el colaborador: " + e.getMessage());
        }
    }

    public void modificarFormaDeColaborar(Long chatId, String mensaje, Comandos comandos) {
        String[] partes = mensaje.split("\\s+");

        int id = Integer.parseInt(partes[0]);
        String formas = partes[1];
        try {
        	formas = formas.concat("\",").concat("\"").concat(partes[2]);
            formas = formas.concat("\",").concat("\"").concat(partes[3]);
        }
        catch (ArrayIndexOutOfBoundsException e) {
        }

        try {
            String requestBody = String.format(
                    "{\"formas\": [\"%s\"]}",
                    formas
            );
            String uri = String.format("/colaboradores/%d",
                    id
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + uri))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                comandos.sendMessage(chatId, "Forma cambiada exitosamente");
                System.out.println("Forma cambiada exitosamente: " + response.body());
            } else {
                comandos.sendMessage(chatId,"Error al cambiar forma");
                System.out.println("Error al cambiar forma: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            comandos.sendMessage(chatId,"Ocurrió un error al cambiar forma");
            System.out.println("Ocurrió un error al cambiar forma: " + e.getMessage());
        }
    }

    public void reportarHeladera(Long chatId, String mensaje, Comandos comandos) {
        String[] partes = mensaje.split("\\s+");

        int id = Integer.parseInt(partes[0]);
        try {
            String requestBody = "";
            String uri = String.format("/colaboradores/%d/reportar",
                    id
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + uri))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                comandos.sendMessage(chatId, "Forma cambiada exitosamente");
                System.out.println("Forma cambiada exitosamente: " + response.body());
            } else {
                comandos.sendMessage(chatId, "Error al cambiar forma");
                System.out.println("Error al cambiar forma: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            comandos.sendMessage(chatId, "Ocurrió un error al cambiar forma");
            System.out.println("Ocurrió un error al cambiar forma: " + e.getMessage());
        }
    }

    public void repararHeladera(Long chatId, String mensaje, Comandos comandos) {
        String[] partes = mensaje.split("\\s+");

        int id = comandos.getIdColaboradorActual();;
        int heladeraid = Integer.parseInt(partes[0]);
        try {
            String requestBody = "";
            String uri = String.format("/colaboradores/%d/reparar/%d",
                    id,heladeraid
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + uri))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                comandos.sendMessage(chatId, "Heladera reparada exitosamente");
                System.out.println("Heladera reparada exitosamente: " + response.body());
            } else {
                comandos.sendMessage(chatId, "Error al reparar heladera");
                System.out.println("Error al reparar heladera: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            comandos.sendMessage(chatId, "Ocurrió un error al reparar heladera");
            System.out.println("Ocurrió un error al reparar heladera: " + e.getMessage());
        }
    }

    public void verMisDatos(Long chatId, Comandos comandos) {
    	//String[] partes = mensaje.split("\s+");

        int id = comandos.getIdColaboradorActual();
        try {
            String uriPuntos = String.format("/colaboradores/%d/puntos", id);
            String uriColaborador = String.format("/colaboradores/%d", id);

            HttpRequest requestPuntos = HttpRequest.newBuilder()
                    .uri(URI.create(url + uriPuntos))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpRequest requestColaborador = HttpRequest.newBuilder()
                    .uri(URI.create(url + uriColaborador))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> responsePuntos = client.send(requestPuntos, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responseColaborador = client.send(requestColaborador, HttpResponse.BodyHandlers.ofString());

            System.out.println(responseColaborador.body());
            
            DatosColaboradorDTO datosColaborador = parseDatosColaborador(responseColaborador.body());
            
            comandos.sendMessage(chatId, "Id de colaborador: " + datosColaborador.getId()
            							+ "\n Nombre del colaborador: " + datosColaborador.getNombre()
                                        + "\n Formas de colaboracion: " + datosColaborador.getFormas()
                                        + "\n Pesos Donados: " + datosColaborador.getPesosDonados()
                                        + "\n Heladeras Reparadas: " + datosColaborador.getHeladerasReparadas()
            							+ "\n Cantidad de Puntos: " + responsePuntos.body());
            System.out.println("Id de colaborador: " + datosColaborador.getId()
                    + "\n Nombre del colaborador: " + datosColaborador.getNombre()
                    + "\n Formas de colaboracion: " + datosColaborador.getFormas()
                    + "\n Pesos Donados: " + datosColaborador.getPesosDonados()
                    + "\n Heladeras Reparadas: " + datosColaborador.getHeladerasReparadas()
                    + "\n Cantidad de Puntos: " + responsePuntos.body());
        } catch (Exception e) {
            e.printStackTrace();
            comandos.sendMessage(chatId, "Ocurrió un error al buscar puntos");
            System.out.println("Ocurrió un error al buscar puntos: " + e.getMessage());
        }

    }
    
    public static DatosColaboradorDTO parseDatosColaborador(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        	DatosColaboradorDTO datosColaborador = objectMapper.readValue(responseBody, DatosColaboradorDTO.class);
        	return datosColaborador;
        } catch (IOException e) {
          e.printStackTrace();
          return null;
        }
      }
    
    public void suscribirse(Long chatId, String mensaje, Comandos comandos) {
    	String[] partes = mensaje.split("\\s+");

    	int colaboradorId = Integer.parseInt(partes[0]);
    	int heladeraId = Integer.parseInt(partes[1]);
    	String tipoSuscripcion = partes[2];
    	int cantidadN = Integer.parseInt(partes[3]);
        
        try {
            String requestBody = String.format(
                    "{\"colaboradorId\": %d, \"heladeraId\": %d, \"tipoSuscripcion\": \"%s\", \"cantidadN\": %d}",
                    colaboradorId, heladeraId, tipoSuscripcion, cantidadN
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/colaboradores/suscribirse"))
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
            case 201:
            	comandos.sendMessage(chatId, "Se logro la suscripcion excitosamente");
                System.out.println("Se logro la suscripcion excitosamente: " + response.body());
                break;
            default:
            	comandos.sendMessage(chatId, "Hubo un error en la solicitud");
                System.out.println("Hubo un error en la solicitud: " + response.statusCode() + " - " + response.body());
            	break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            comandos.sendMessage(chatId,"Ocurrió un error al intentar de suscribirse");
            System.out.println("Ocurrió un error al intentar de suscribirse: " + e.getMessage());
        }
    }
    
    
    
}


