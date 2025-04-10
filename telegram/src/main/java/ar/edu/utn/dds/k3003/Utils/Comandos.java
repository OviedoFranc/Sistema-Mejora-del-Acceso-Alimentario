package ar.edu.utn.dds.k3003.Utils;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ar.edu.utn.dds.k3003.app.WebApp.ChatIdRegistry;
import java.util.HashMap;
import java.util.Map;

public class Comandos extends TelegramLongPollingBot {
    private BotLogistica botLogistica;
    private BotHeladera botHeladera;
    private BotVianda botViandas;
    private BotColaborador botColaborador;
    private BotSensor botSensor;
    private Map<Long, String> esperandoUsuarios = new HashMap<>();
    private int idColaboradorActual;

    public Comandos() {
        this.botLogistica = new BotLogistica();
        this.botHeladera = new BotHeladera();
        this.botViandas = new BotVianda();
        this.botColaborador = new BotColaborador();
        this.botSensor = new BotSensor();
    }

    public void handleCommand(Long chatId, String command) {
        switch (command) {
            case "/start":
            	esperandoUsuarios.put(chatId, "obtenerColaboradorId");
                sendMessage(chatId, "¡Hola! Soy tu bot. Por favor escribe tu numero de colaboradorId.");
                break;
            case "/menu":
                showMenu(chatId);
                break;
            case "/darDeAltaRuta":
                esperandoUsuarios.put(chatId, "darDeAltaRuta");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "colaboradorId heladeraOrigenId heladeraDestinoId\n" +
                        "Ejemplo: 5 1 2");
                break;
            case "/asignarTraslado":
                esperandoUsuarios.put(chatId, "asignarTraslado");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "listQrViandas heladeraOrigen heladeraDestino\n" +
                        "Ejemplo: [asd] 1 2");
                break;
            case "/iniciarTraslado":
                esperandoUsuarios.put(chatId, "iniciarFinalizarTraslado");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "status idTraslado\n" +
                        "Ejemplo: EN_VIAJE 1");
                break;
            case "/finalizarTraslado":
                esperandoUsuarios.put(chatId, "iniciarFinalizarTraslado");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "status\n" +
                        "Ejemplo: ENTREGADO 1");
                break;

            case "/agregarColaborador":
                esperandoUsuarios.put(chatId, "agregarColaborador");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "nombre FormaDeColaborar\n" +
                        "Ejemplo: pepito DONADOR");
                break;

            case  "/modificarFormaDeColaborar":
                esperandoUsuarios.put(chatId, "modificarFormaDeColaborar"); // FUNCIONA???
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato (puede elegir mas de una):\n" +
                        "idcolaborador formaDeColaborar\n" +
                        "Ejemplo:  1 “DONADOR“ “TRANSPORTADOR“ “TECNICO“");
                break;
            case  "/reportarHeladeraRota":
                esperandoUsuarios.put(chatId, "reportarHeladeraRota");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato :\n" +
                        "heladeraID\n" +
                        "Ejemplo: 1");
                break;
            case   "/repararHeladera":
                esperandoUsuarios.put(chatId, "repararHeladera");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "heladeraID\n" +
                        "Ejemplo: 1");
                break;
            case   "/verMisDatos":
                botColaborador.verMisDatos(chatId, this);
                break;
            case   "/crearYDepositarVianda":
                esperandoUsuarios.put(chatId, "crearYDepositarVianda");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "codigoQR fechaElaboracion idColaborador heladeraId\n" +
                        "Ejemplo: abc 2024-05-09T10:30:00Z 1 2");
                break;
            case   "/retirarVianda":
                esperandoUsuarios.put(chatId, "retirarVianda");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "codigoQR heladeraId\n" +
                        "Ejemplo: abc 2");
                break;
            case   "/verIncidentesDeHeladera":
                esperandoUsuarios.put(chatId, "verIncidentesDeHeladera");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "heladeraId\n" +
                        "Ejemplo: 2");
                break;
            case   "/verOcupacion":
                esperandoUsuarios.put(chatId, "verOcupacion");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "heladeraId\n" +
                        "Ejemplo: 2");
                break;
            case   "/verRetirosDelDia":
                esperandoUsuarios.put(chatId, "verRetirosDelDia");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "heladeraId\n" +
                        "Ejemplo: 2");
                break;
            case   "/eliminarSuscripcion":
                esperandoUsuarios.put(chatId, "eliminarSuscripcion");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "tipoDeSuscripcion heladeraId colaboradorId\n" +
                        "Ejemplo: “ViandasDisponibles“ “FaltanteViandas“ “HeladeraDesperfecto“ 2 1");
                break;
            case   "/suscribirse":
                esperandoUsuarios.put(chatId, "suscribirse");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato:\n" +
                        "colaboradorId heladeraId tipoDeSuscripcion cantidadN\n" +
                        "Ejemplo: 1 2 “ViandasDisponibles“ 2");
                break;
            case "/pararSensor":
                esperandoUsuarios.put(chatId, "pararSensor");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato: heladeraId\n" +
                        "Ejemplo: 1");
                break;
            case "/reanudarSensor":
                esperandoUsuarios.put(chatId, "reanudarSensor");
                sendMessage(chatId, "Por favor, envía los datos en el siguiente formato: heladeraId\n" +
                        "Ejemplo: 1");
                break;
            default:
                sendMessage(chatId, "Comando no reconocido.");
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMenu(Long chatId) {
        String menuText = "Elige una opción:\n\n" +
        		"LOGISTICA:\n" +
                "/darDeAltaRuta - Crear ruta\n" +
                "/asignarTraslado - asigna un traslado a un colaborador\n" +
                "/iniciarTraslado - inicia un traslado de una vianda\n" +
                "/finalizarTraslado - finaliza el traslado de una vianda\n\n" +
                "COLABORADORES:\n" +
                "/agregarColaborador\n" +
                "/modificarFormaDeColaborar\n" +
                "/reportarHeladeraRota\n" +
                "/repararHeladera - reporta que la heladera ha sido reparada\n" +
                "/verMisDatos\n" +     //Si me da tiempo agregar forma de ver reparaciones, donaciones de dinero, etc
                "/suscribirse - Se suscribe a un tipo de evento de heladera\n\n" +
                "VIANDAS:\n" +
                "/crearYDepositarVianda - Crea y Deposita una vianda en una heladera\n\n" +
                "HELADERAS:\n" +
                "/retirarVianda - Retira una vianda de una heladera\n" +
                "/verIncidentesDeHeladera - Devuelve el historial de incidentes de una heladera\n" +
                "/verOcupacion - Devuelve una lista de las viandas dentro de la heladera\n" + 
                "/verRetirosDelDia - Devuelve una lista de las viandas retiradas\n" +
                "/eliminarSuscripcion - Elimina la suscripcion de una heladera\n" +
                "/pararSensor - Para el sensor de una heladera, esto genera error y es para probar la cola de mensajes\n"+
                "/reanudarSensor - Reanuda el sensor de una heladera, es para probar la cola de mensajes\n";
        sendMessage(chatId, menuText);
    }
    
    public void obtenerColaboradorId(Long chatId, String mensaje, Comandos comandos) {
    	idColaboradorActual = Integer.parseInt(mensaje);
    	ChatIdRegistry.registrarChatId(idColaboradorActual, chatId);
    	comandos.sendMessage(chatId, "Bienvenido colaborador nro " + idColaboradorActual + ".  Use el comando /menu para ver las funcionalidades.");
        System.out.println("Colaborador nro " + idColaboradorActual + " entro al bot");
    }

    public void onMessageReceived1(Long chatId, String message) {
        String estado = esperandoUsuarios.getOrDefault(chatId, "");

        switch (estado) {
	        case "obtenerColaboradorId":
	        	obtenerColaboradorId(chatId, message, this);
	            break;
            case "darDeAltaRuta":
                botLogistica.darDeAltaRuta(chatId, message, this);
                break;
            case "asignarTraslado":
               botLogistica.asignarTraslado(chatId, message, this);
                break;
            case "iniciarFinalizarTraslado":
               botLogistica.iniciarFinalizarTraslado(chatId, message, this);
                break;
            case "agregarColaborador":
            	botColaborador.agregarColaborador(chatId, message, this);
                break;
            case "modificarFormaDeColaborar":
            	botColaborador.modificarFormaDeColaborar(chatId, message, this);
                break;
            case "reportarHeladeraRota":
            	botColaborador.reportarHeladera(chatId, message, this);
                break;
            case "repararHeladera":
            	botColaborador.repararHeladera(chatId, message, this);
                break;
            case "suscribirse":
            	botColaborador.suscribirse(chatId, message, this);
                break;
            case "crearYDepositarVianda":
                botViandas.crearYDepositarVianda(chatId, message, this);
                break;
            case "retirarVianda":
            	botHeladera.retirarVianda(chatId, message, this);
                break;
            case "verIncidentesDeHeladera":
            	botHeladera.verIncidentesDeHeladera(chatId, message, this);
                break;
            case "verOcupacion":
            	botHeladera.verOcupacion(chatId, message, this);
                break;
            case "verRetirosDelDia":
            	botHeladera.verRetirosDelDia(chatId, message, this);
                break;
            case "eliminarSuscripcion":
            	botHeladera.eliminarSuscripcion(chatId, message, this);
                break;
            case "pararSensor":
                botSensor.pararSensor(chatId, message, this);
            case "reanudarSensor":
                botSensor.reanudarSensor(chatId, message, this);
        }
    }
    
    public Integer getIdColaboradorActual() {
    	return idColaboradorActual;
    }

    @Override
    public void onUpdateReceived(Update update) {
    }

    @Override
    public String getBotUsername() {
        return System.getenv().get("Nombre_Bot");
    }

    @Override
    public String getBotToken() {
        return System.getenv().get("BOT_TOKEN");
    }
}

