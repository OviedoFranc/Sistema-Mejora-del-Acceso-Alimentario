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

    public static void main(String[] args) throws Exception {
        Dotenv dotenv = Dotenv.load();

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
    }
}
