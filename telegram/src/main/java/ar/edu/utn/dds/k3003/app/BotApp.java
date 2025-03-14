package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.Utils.Comandos;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class BotApp extends TelegramLongPollingBot  {

    private Comandos commandsHandler;

    public static void main(String[] args) throws Exception {
        Dotenv dotenv = Dotenv.load();

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(new BotApp());
            System.out.println("ya esta corriendo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BotApp() {
        this.commandsHandler = new Comandos();
    }
    
    public class ChatIdRegistry {
    	private static Map<Integer, Long> chatIds = new HashMap<>();
    	
    	public static void registrarChatId(int colaboradorId, Long chatId) {
    		chatIds.put(colaboradorId, chatId);
    	}
    	
    	public static Long obtenerChatId(int colaboradorId) {
    		return chatIds.get(colaboradorId);
    	}
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/")) {
                commandsHandler.handleCommand(chatId, messageText);
            } else {
                commandsHandler.onMessageReceived1(chatId, messageText);
            }
        }
    }
//
    @Override
    public String getBotUsername() {
        return Dotenv.load().get("Nombre_Bot");
    }

    @Override
    public String getBotToken() {
        return Dotenv.load().get("BOT_TOKEN");
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
