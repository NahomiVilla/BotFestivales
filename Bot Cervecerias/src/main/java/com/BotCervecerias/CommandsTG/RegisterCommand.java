package com.BotCervecerias.CommandsTG;

import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Services.MyTelegramBot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class RegisterCommand {




    public  void handleRegistration(String messageText,Long chatId, MyTelegramBot bot){

        String[] parts=messageText.split(" ");

        if (parts.length<4){
            bot.sendMessage(chatId,"Formato no valido. Usa: nombre correo contraseña tipo_de_usuario");
            return;
        }
        String nombre=parts[0];
        String correo=parts[1];
        String password =parts[2];
        Long userType= Long.parseLong(parts[3]);
        //System.out.println(userType);

        Users newUser=new Users();
        newUser.setName(nombre);
        newUser.setEmail(correo);
        newUser.setPassword(password);
        newUser.setUsersTypeId(userType);

        try {
            RestTemplate restTemplate=new RestTemplate();
            String url="http://localhost:8080/api/auth/register";
            ResponseEntity<String> response=restTemplate.postForEntity(url,newUser,String.class);
            if (response.getStatusCode()== HttpStatus.OK){
                bot.sendMessage(chatId,"Registro exitoso");
            }
        }catch (Exception e){
            bot.sendMessage(chatId,"Error en el registro: "+ e.getMessage());
        }
    }



}
