package com.BotCervecerias;

import com.BotCervecerias.Services.MyTelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import  org.telegram.telegrambots.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class MyBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyBotApplication.class,args);
        try {
            // Inicializar el contexto de Telegram y registrar el bot
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyTelegramBot());

            System.out.println("Bot iniciado con éxito.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}