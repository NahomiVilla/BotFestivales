package com.BotCervecerias.Services;

import com.BotCervecerias.CommandsTG.RegisterBeers;
import com.BotCervecerias.CommandsTG.RegisterCommand;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class MyTelegramBot extends TelegramLongPollingBot {


    private  RegisterCommand registerCommand=new RegisterCommand() ;
    private RegisterBeers registerBeerCommand=new RegisterBeers();
    private Map<Long, String> userState = new HashMap<>();

    @Override
    public String getBotUsername() {
        return "BreweriessBot";
    }

    @Override
    public String getBotToken() {
        return "7039313046:AAF2AvsR_JinSsRxrHty4oJDKgh21Q7u6cI";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (userState.containsKey(chatId)) {
                String state = userState.get(chatId);

                if (state.equals("register")) {
                    // Procesar los datos de registro de usuario
                    registerCommand.handleRegistration(messageText, chatId, this);
                    userState.remove(chatId); // Limpiar estado después del registro
                } else if (state.equals("Beerregister")) {
                    // Procesar los datos de registro de cerveza
                    registerBeerCommand.handleBeerRegistration(messageText, chatId, this);
                    userState.remove(chatId); // Limpiar estado después del registro
                }
            } else {
                if (messageText.startsWith("/Beerregister")) {
                    sendMessage(chatId, """
                            Ingresa los detalles de la cerveza en el siguiente formato:
                            Nombre Tipo GradoAlcohol BTU Descripción URLImagen""");
                    userState.put(chatId, "Beerregister");

                }else if (messageText.startsWith("/register")) {
                    sendMessage(chatId, """
                            ingrese los siguientes datos:
                            Nombre
                            Correo
                            Tipo de Usuario(1=Organización o 2=Cervecería)""");
                    userState.put(chatId, "register");
                }
            }
        }
    }
    public void sendMessage (Long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
