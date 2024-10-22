package com.BotCervecerias.CommandsTG;

import com.BotCervecerias.Models.Beers;
import com.BotCervecerias.Models.BeersType;
import com.BotCervecerias.Models.Companies;
import com.BotCervecerias.Response.JwtResponse;
import com.BotCervecerias.Services.MyTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class BeersCommands  {
    @Autowired
    private JwtResponse jwtResponse;

    public void handleBeerRegistration(String messageText, Long chatId, MyTelegramBot bot,String token) {
        String[] parts = messageText.split("-", 6); // Limitar la división para que la descripción y la URL de imagen puedan contener espacios

        if (parts.length < 6) {
            bot.sendMessage(chatId, "Formato no válido. Usa: Nombre-Tipo-GradoAlcohol-BTU-Descripción-URLImagen");
            return;
        }

        String nombre = parts[0].toUpperCase();
        Long tipo = Long.parseLong(parts[1]);
        Double gradoAlcohol = Double.parseDouble(parts[2]);
        Double btu = Double.parseDouble(parts[3]);
        String descripcion = parts[4].toUpperCase();
        String urlImagen = parts[5];

        Beers newBeer = new Beers();
        newBeer.setName(nombre);
        newBeer.setBeersTypeId(tipo);
        newBeer.setAlcohol_grad(gradoAlcohol);
        newBeer.setBtu(btu);
        newBeer.setDescription(descripcion);
        newBeer.setImage(urlImagen);

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/api/beer/BeerRegister";
            if (token==null|| token.isEmpty()){
                bot.sendMessage(chatId,"error: no se encontro un token de autenticacion.inicia sesion primero");
                return;
            }
            HttpHeaders headers=new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Beers>request=new HttpEntity<>(newBeer,headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                bot.sendMessage(chatId, "Cerveza registrada exitosamente");
            }
        } catch (Exception e) {
            bot.sendMessage(chatId, "Error en el registro de cerveza: " + e.getMessage());
        }
    }

    public void handleVerInfoCerveza(String messageText,Long chatId,MyTelegramBot bot,String token){
        String[] parts=messageText.split("-");
        String beer_name=parts[1];

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/api/beer/VerInfoBeer/"+beer_name.toUpperCase();
            if (token==null|| token.isEmpty()){
                bot.sendMessage(chatId,"error: no se encontro un token de autenticacion.inicia sesion primero");
                return;
            }
            HttpHeaders headers=new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String>request=new HttpEntity<>(headers);
            ResponseEntity<Beers> response = restTemplate.exchange(url, HttpMethod.GET, request, Beers.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Beers cerveza=response.getBody();
                assert cerveza != null;
                String messageBeerInfo= "Cerveza encontrada:\n\n"+
                            "Nombre: "+cerveza.getName()+"\n"+
                            "Tipo de cerveza: "+cerveza.getBeersTypeName()+"\n"+
                            "Grados de alcohol: "+cerveza.getAlcohol_grad()+"\n"+
                            "Btu: "+cerveza.getBtu()+"\n"+
                            "Descripcion: "+cerveza.getDescription()+"\n"+
                            "Logo: \n\n"+cerveza.getImage();
                bot.sendMessage(chatId,messageBeerInfo);
            }

        } catch (Exception e) {
            bot.sendMessage(chatId, "Error en el registro de cerveza: " + e.getMessage());
        }
    }
    public void handleGetAllBeers(Long chatId,MyTelegramBot bot,String token){

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/api/beer/AllBeersInfo";
            if (token==null|| token.isEmpty()){
                bot.sendMessage(chatId,"error: no se encontro un token de autenticacion.inicia sesion primero");
                return;
            }
            HttpHeaders headers=new HttpHeaders();
            headers.setBearerAuth(token);

            HttpEntity<String>request=new HttpEntity<>(headers);
            ResponseEntity<Beers[]> response = restTemplate.exchange(url, HttpMethod.GET, request, Beers[].class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Beers[] cervezas=response.getBody();
                assert cervezas != null && cervezas.length!=0;
                for (Beers cerveza:cervezas){
                    String messageBeerInfo= "Cerveza encontrada:\n\n"+
                            "Nombre: "+cerveza.getName()+"\n"+
                            "Tipo de cerveza: "+cerveza.getBeersTypeName()+"\n"+
                            "Grados de alcohol: "+cerveza.getAlcohol_grad()+"\n"+
                            "Btu: "+cerveza.getBtu()+"\n"+
                            "Descripcion: "+cerveza.getDescription()+"\n"+
                            "Logo: \n\n"+cerveza.getImage()+"\n"+
                            "Cerveceria a la que pertenece: "+cerveza.getCompaniesName();
                    bot.sendMessage(chatId,messageBeerInfo);
                }
            }

        } catch (Exception e) {
            bot.sendMessage(chatId, "Error en el registro de cerveza: " + e.getMessage());
        }
    }

    public void handleDeleteBeer(String messageText,Long chatId, MyTelegramBot bot,String token) {
        if (!messageText.equalsIgnoreCase("NO")) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                String urlDelete = "http://localhost:8080/api/beer/DeleteBeer/"+messageText.toUpperCase();
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);

                HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);

                ResponseEntity<String> responseDelete = restTemplate.exchange(urlDelete, HttpMethod.DELETE, deleteRequest, String.class);
                if (responseDelete.getStatusCode() == HttpStatus.OK) {
                    bot.sendMessage(chatId, "cerveza eliminada con exito");
                } else {
                    bot.sendMessage(chatId, "Error al eliminar la compañia: " + responseDelete.getStatusCode());
                }

            } catch (Exception e) {
                bot.sendMessage(chatId, "Error al eliminar cerveza: " + e.getMessage());
            }
        } else {
            bot.sendMessage(chatId, "Accion cancelada");
        }
    }

    public void handleUpdateInfoBeer(String nombreBeer,String messageText,Long chatId, MyTelegramBot bot,String token){
        String[] parts=messageText.split("-");
        try {
            RestTemplate restTemplate=new RestTemplate();
            String urlGet="http://localhost:8080/api/beer/VerInfoBeer/"+nombreBeer.toUpperCase();
            if (token == null || token.isEmpty()) {
                bot.sendMessage(chatId, "error: no se encontro un token de autenticacion.inicia sesion primero");
                return;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Beers> responseGet=restTemplate.exchange(urlGet,HttpMethod.GET,request,Beers.class);
            if(responseGet.getStatusCode()==HttpStatus.OK){
                Beers beerInfo=responseGet.getBody();
                if (beerInfo!=null){
                    String nuevaInfo=parts[1].toUpperCase();
                    if (messageText.startsWith("/editName")){

                        beerInfo.setName(nuevaInfo);
                    } else if (messageText.startsWith("/editDescription")) {
                        beerInfo.setDescription(nuevaInfo);
                    } else if (messageText.startsWith("/editImage")) {
                        beerInfo.setImage(nuevaInfo);
                    } else if (messageText.startsWith("/editType")) {

                        beerInfo.setBeersTypeName(nuevaInfo);
                    }

                    String url="http://localhost:8080/api/beer/EditInfoBeer/"+nombreBeer.toUpperCase();
                    HttpEntity<Beers>putRequest=new HttpEntity<>(beerInfo,headers);
                    ResponseEntity<Beers> response=restTemplate.exchange(url,HttpMethod.PUT,putRequest,Beers.class);
                    if (response.getStatusCode() == HttpStatus.OK) {
                        bot.sendMessage(chatId, "informacion de Cerveza guardada con exito");
                    } else {
                        bot.sendMessage(chatId, "Error al guardar la información de la compañía: " + response.getStatusCode());
                    }
                }else {
                    bot.sendMessage(chatId, "No se encontró la información actual de la cerveza.");
                }
            }else {
                bot.sendMessage(chatId, "Error al obtener la información de la cerveza: " + responseGet.getStatusCode());
            }
        }catch (Exception e){
            bot.sendMessage(chatId,"Error al hacer el cambio de informacion: "+e.getMessage());
        }
    }
}
