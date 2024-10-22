package com.BotCervecerias.CommandsTG;

import com.BotCervecerias.Response.JwtResponse;
import com.BotCervecerias.Services.MyTelegramBot;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class LoginCommands {


    public JwtResponse handleLogin(String messageText, Long chatId, MyTelegramBot bot){
        System.out.println("LoginCommands iniciado...");
        String[] loginData=messageText.split("-");
        if (loginData.length==2){
            String email=loginData[0];
            String password=loginData[1];
            JwtResponse  tokenResponse=authenticateUser(email,password,chatId,bot);
            if (tokenResponse!=null && tokenResponse.getToken()!=null){
                bot.sendMessage(chatId,"Inicio de sesion exitoso");
                if (tokenResponse.getRoles()[0].equals("ROLE_ORGANIZACION")){
                    bot.sendMessage(chatId, """
                            Como organizacion tiene acceso a los siguientes comandos:
                            
                            #COMANDOS DE USUARIO
                            /EditInfoUser (para haccer la edicion de los datos de usuario )
                            /DeleteUser (para eliminar su usuario)
                            
                            #COMANDOS DE ORGANIZACIÓN
                            /EditInfoCompany (para hacer la edicion de los datos de la organización)
                            /VerInfoCompany (para visualizar los datos de la organización)
                            
                            #COMANDOS DE EVENTOS
                            /EventRegister
                            /EditEvent-[nombre del evento] (para editar los datos de un evento ya registrado)
                            /InfoEvent-[nombre del evento] (para ver la informacion de l evento con el nombre ingresado)
                            /InfoAllEvents (para ver la informacion de tos los eventos creados por la organización)
                            /DeleteEvent (para el eliminar un evento)
                            """);
                } else if (tokenResponse.getRoles()[0].equals("ROLE_CERVECERIA")) {
                    bot.sendMessage(chatId,"""
                            Como cervecería tiene acceso a los siguientes comandos:
                            
                            #COMANDOS DE USUARIO
                            /EditInfoUser (para haccer la edicion de los datos de usuario )
                            /DeleteUser (para eliminar su usuario)
                            
                            #COMANDOS DE CERVECERIA
                            /EditInfoCompany (para hacer la edicion de los datos de la cervecería)
                            /VerInfoCompany (para visualizar los datos de la cervecería)
                            
                            #COMANDOS DE CERVEZAS
                            /Beerregister
                            /EditInfoBeer-[nombre del cerveza] (para editar los datos de un evento ya registrado)
                            /DeleteBeer (para eliminar una cerveza del registro)
                            /VerInfoCerveza-[nombre del cerveza] (para ver la informacion de todas las cervezas registradas por la cervecería)
                            /AllBeers (para ver las informacion de todas las cervezas registradas por la cervecería
                            """);
                }
                return tokenResponse;
            }else {
                bot.sendMessage(chatId,"correo o congtraseña invalidos");
                return null;
            }
        }else {
            bot.sendMessage(chatId,"Formato incorrecto");
            return  null;
        }

    }
    private JwtResponse authenticateUser(String email, String password,Long chatId,MyTelegramBot bot) {


            try {
                URL url = new URL("http://localhost:8080/login");
                HttpURLConnection con=(HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                con.setDoOutput(true);

                String jsonInputString="{\"email\": \"" +email+"\", \"password\": \""+password+"\"}";

                try(OutputStream out =con.getOutputStream()){
                    byte[] input=jsonInputString.getBytes(StandardCharsets.UTF_8);
                    out.write(input,0,input.length);
                }
                int status=con.getResponseCode();
                if (status==HttpURLConnection.HTTP_OK){
                    BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder content=new StringBuilder();
                    while ((inputLine=in.readLine())!=null){
                        content.append(inputLine);
                    }
                    in.close();
                    Gson gson=new Gson();
                    JwtResponse jwtResponse=gson.fromJson(content.toString(),JwtResponse.class);
                    String token =jwtResponse.getToken();
                    System.out.println("Respuesta del servidor: " + content.toString());

                    return  jwtResponse;
                }else {
                    bot.sendMessage(chatId, "Error en el login, código de respuesta: " + status);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                bot.sendMessage(chatId, "Ocurrió un error al autenticar: " + e.getMessage());
                return null;
            }

    }
}

