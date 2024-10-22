package com.BotCervecerias.CommandsTG;

import com.BotCervecerias.Models.Companies;
import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Response.JwtResponse;
import com.BotCervecerias.Services.MyTelegramBot;
import com.google.gson.Gson;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class UserCommands {

    public  void handleRegistration(String messageText,Long chatId, MyTelegramBot bot){

        String[] parts=messageText.split("-");

        if (parts.length<4){
            bot.sendMessage(chatId,"Formato no valido. Usa: nombre-correo-contraseña-tipo de usuario");
            return;
        }
        String nombre=parts[0];
        String correo=parts[1];
        String password =parts[2];
        Long userType= Long.parseLong(parts[3]);

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
                bot.sendMessage(chatId,"Registro exitoso" +
                        "Edite la informacion de su negocio para poder hacer uso de los comandos correspondientes, lo puede hacer con el siguiente comando /editInfo");
            }
        }catch (Exception e){
            bot.sendMessage(chatId,"Error en el registro: "+ e.getMessage());
        }
    }

    public  void handleChangeInfoUser(String messageText,Long chatId,MyTelegramBot bot,String token){
        String[] parts=messageText.split("-",3);
        if (parts.length<2){
            bot.sendMessage(chatId,"Formato no valido.");
            return;
        }
        try{
            RestTemplate restTemplate=new RestTemplate();
            String url="http://localhost:8080/api/users/InfoUser";
            if (token == null || token.isEmpty()) {
                bot.sendMessage(chatId, "error: no se encontro un token de autenticacion.inicia sesion primero");
                return;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Users> responseGet = restTemplate.exchange(url, HttpMethod.GET, request, Users.class);

            if (responseGet.getStatusCode()==HttpStatus.OK){
                Users usersInfo=responseGet.getBody();
                if (usersInfo!=null){
                    String nuevaInfo=parts[1];
                    if (messageText.startsWith("/editPassword")){
                        usersInfo.setPassword(nuevaInfo);
                    }else if (messageText.startsWith("/editName")){
                        usersInfo.setName(nuevaInfo);
                    }
                    String urlPut="http://localhost:8080/api/users/editInfo/"+usersInfo.getId();
                    HttpEntity<Users> putRequest=new HttpEntity<>(usersInfo,headers);
                    ResponseEntity<String> responsePut=restTemplate.exchange(urlPut,HttpMethod.PUT,putRequest,String.class);
                    if (responsePut.getStatusCode()==HttpStatus.OK){
                        bot.sendMessage(chatId,"informacion actualizada");
                    }else {
                        bot.sendMessage(chatId,"error al actualizar informacion");
                    }
                }else {
                    bot.sendMessage(chatId,"no se encontró la informacion actual del usuario");
                }
            }else {
                bot.sendMessage(chatId,"error al obtener ela informacion del ususario");
            }

        } catch (Exception e) {
            bot.sendMessage(chatId,"error al hacer el cambio de informacion: "+e.getMessage());
        }


    }

}
