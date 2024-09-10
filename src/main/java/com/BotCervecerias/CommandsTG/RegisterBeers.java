package com.BotCervecerias.CommandsTG;

import com.BotCervecerias.Models.Beers;
import com.BotCervecerias.Services.MyTelegramBot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RegisterBeers  {

    public void handleBeerRegistration(String messageText, Long chatId, MyTelegramBot bot) {
        String[] parts = messageText.split(" ", 6); // Limitar la división para que la descripción y la URL de imagen puedan contener espacios

        if (parts.length < 6) {
            bot.sendMessage(chatId, "Formato no válido. Usa: Nombre Tipo GradoAlcohol BTU Descripción URLImagen");
            return;
        }

        String nombre = parts[0];
        Long tipo = Long.parseLong(parts[1]);
        Boolean gradoAlcohol = Boolean.parseBoolean(parts[2]);
        Boolean btu = Boolean.parseBoolean(parts[3]);
        String descripcion = parts[4];
        String urlImagen = parts[5];

        Beers newBeer = new Beers();
        newBeer.setName(nombre);
        newBeer.setBeersTypeID(tipo);
        newBeer.setAlcohol_grad(gradoAlcohol);
        newBeer.setBtu(btu);
        newBeer.setDescription(descripcion);
        newBeer.setImage(urlImagen);

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/api/auth/BeerRegister";
            ResponseEntity<String> response = restTemplate.postForEntity(url, newBeer, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                bot.sendMessage(chatId, "Cerveza registrada exitosamente");
            }
        } catch (Exception e) {
            bot.sendMessage(chatId, "Error en el registro de cerveza: " + e.getMessage());
        }
    }


}
