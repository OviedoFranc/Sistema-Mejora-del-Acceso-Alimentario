package ar.edu.utn.dds.k3003.utils;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.model.Heladera;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class utilsHeladera {

    public static void crearHeladeras(Fachada fachada){

            HeladeraDTO heladeraNuevaDTO1 = new HeladeraDTO(null, "Heladera1", null);
            HeladeraDTO heladeraNuevaDTO2 = new HeladeraDTO(null, "Heladera2", null);
            HeladeraDTO heladeraNuevaDTO3 = new HeladeraDTO(null, "Heladera3", null);

            //Guardp y obtengo sus nuevos IDs asignados
            fachada.agregar(heladeraNuevaDTO1);
            fachada.agregar(heladeraNuevaDTO2);
            fachada.agregar(heladeraNuevaDTO3);

        }
        public static void borrarTodo(Fachada fachada){
            List<Heladera> heladeras= fachada.obtenerTodasLasHeladeras();
        for(Heladera heladera: heladeras){
            fachada.eliminarHeladera(heladera.getHeladeraId());
        }
    }

        public static void avisoSensorCreacionHeladera(Integer heladeraId){
            String url = System.getenv("URL_SENSOR");
            try {
                String uri = url + "/addSensorHeladera/"+heladeraId;

                // Crear la solicitud GET
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(uri))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                // Envio la solicitud de manera asíncrona
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            // Manejar la respuesta cuando esté disponible
                            if (response.statusCode() == 200) {
                                System.out.println("Se ha agregado correctamente");
                            } else {
                                System.out.println("No se ha agregado correctamente");
                            }
                        })
                        .exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        });
                System.out.println("Solicitud enviada de manera asíncrona.");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public static void avisoSensorEliminacionHeladera(Integer heladeraId){
        String url = System.getenv("URL_SENSOR");
        try {
            String uri = url + "/stopSensorHeladera/"+heladeraId;

            // Crear la solicitud GET
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            // Envio la solicitud de manera asíncrona
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        // Manejar la respuesta cuando esté disponible
                        if (response.statusCode() == 200) {
                            System.out.println("Se ha eliminado correctamente");
                        } else {
                            System.out.println("No se ha eliminado correctamente");
                        }
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
            System.out.println("Solicitud enviada de manera asíncrona.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
