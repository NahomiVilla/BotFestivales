package com.BotCervecerias.CommandsTG;

import com.BotCervecerias.Services.MyTelegramBot;

public class LoginCommands {
    public void handleLogin(String messageText, Long chatId, MyTelegramBot bot){
        String[] loginData=messageText.split(" ");
        if (loginData.length==2){
            String email=loginData[0];
            String password=loginData[1];
            boolean  isAuthenticated=authenticateUser(email,password);
            if (isAuthenticated){
                bot.sendMessage(chatId,"Inicio de sesion exitoso");
            }else {
                bot.sendMessage(chatId,"correo o congtraseña invalidos");
            }
        }else {
            bot.sendMessage(chatId,"Formato incorrecto");
        }
    }
    private boolean authenticateUser(String email, String password) {
        // Aquí iría la lógica para verificar el email y la contraseña en tu base de datos
        // Ejemplo simple: si el correo es "test@example.com" y la contraseña es "password123"
        if (email.equals("test@example.com") && password.equals("password123")) {
            return true;
        }
        return false;
    }
}
