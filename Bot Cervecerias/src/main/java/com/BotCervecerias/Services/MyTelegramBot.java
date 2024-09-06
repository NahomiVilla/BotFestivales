package com.BotCervecerias.Services;

import com.BotCervecerias.Models.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyTelegramBot extends TelegramLongPollingBot {
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
        if (update.hasMessage()&&update.getMessage().hasText()){
            String messageText=update.getMessage().getText();
            Long chatId=update.getMessage().getChatId();

            if (messageText.startsWith("/register")){
                sendMessage(chatId, """
                        ingrese los siguientes datos:
                        Nombre
                        Correo
                        Tipo de Usuario(Organización o Cervecería""");

                handleRegistration(messageText,chatId);
            }
        }
    }

    private  void handleRegistration(String messageText,Long chatId){
        String[] parts=messageText.split(" ");
        if (parts.length<5){
            sendMessage(chatId,"Formato no valido. Usa: /register nombre correo contraseña tipo_de_usuario");
            return;
        }
        String nombre=parts[1];
        String correo=parts[2];
        String password =parts[3];
        String userType=parts[4].toUpperCase();

        Users newUser=new Users();
        newUser.setName(nombre);
        newUser.setEmail(correo);
        newUser.setPassword(password);
        newUser.setUsersTypeName(String.valueOf(userType.equals("CERVECERIA")?1:2));

        try {
            RestTemplate restTemplate=new RestTemplate();
            String url="http://localhost:8080/api/auth/register";
            ResponseEntity<Users> response=restTemplate.postForEntity(url,newUser,Users.class);
            if (response.getStatusCode()== HttpStatus.OK){
                sendMessage(chatId,"Registro exitoso");
            }
        }catch (Exception e){
            sendMessage(chatId,"Error en el registro: "+ e.getMessage());
        }
    }


    private void sendMessage(Long chatId, String text) {
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
