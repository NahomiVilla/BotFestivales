package com.BotCervecerias.CommandsTG;

import com.BotCervecerias.Models.Companies;

import com.BotCervecerias.Response.JwtResponse;
import com.BotCervecerias.Services.MyTelegramBot;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;


@Component
public class CompanyCommands {
    public  void handleDeleteCompany(String messageText,Long chatId, MyTelegramBot bot, String token){
        if (!messageText.equalsIgnoreCase("NO")){
            try{
                RestTemplate restTemplate=new RestTemplate();
                String url="http://localhost:8080/api/companies/getInfoCompanies";
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                HttpEntity<Void> request = new HttpEntity<>(headers);
                ResponseEntity<Companies> responseGet = restTemplate.exchange(url, HttpMethod.GET, request, Companies.class);

                if (responseGet.getStatusCode() == HttpStatus.OK) {
                    Companies currentCompanyInfo = responseGet.getBody();
                    if (currentCompanyInfo != null) {

                        String urlDelete = "http://localhost:8080/api/companies/deleteCompany";
                        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlDelete)
                                .queryParam("id", currentCompanyInfo.getId());
                        String uri=builder.toUriString();
                        HttpEntity<Companies> deleteRequest = new HttpEntity<>(headers);

                        ResponseEntity<String> responseDelete = restTemplate.exchange(uri, HttpMethod.DELETE, deleteRequest, String.class);
                        if (responseDelete.getStatusCode() == HttpStatus.OK) {
                            bot.sendMessage(chatId, "compañia eliminada con exito");
                        } else {
                            bot.sendMessage(chatId, "Error al eliminar la compañia: " + responseDelete.getStatusCode());
                        }
                    } else {
                        bot.sendMessage(chatId, "No se encontró la información actual de la compañía.");
                    }

                } else {
                    bot.sendMessage(chatId, "Error al obtener la información de la compañía: " + responseGet.getStatusCode());
                }
            }catch (Exception e){
                bot.sendMessage(chatId, "Error al eliminar compañia: " + e.getMessage());
            }
        }else {
            bot.sendMessage(chatId, "Acción cancelada");

        }


    }

    public void handleGetInfoCompany(Long chatId, MyTelegramBot bot, JwtResponse token){
        try{
            RestTemplate restTemplate=new RestTemplate();
            String url="http://localhost:8080/api/companies/getInfoCompanies";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.getToken());
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Companies> responseGet = restTemplate.exchange(url, HttpMethod.GET, request, Companies.class);

            if (responseGet.getStatusCode() == HttpStatus.OK) {
                Companies currentCompanyInfo = responseGet.getBody();
                if (currentCompanyInfo != null) {
                    String companyInfo=String.format("Información de Compañia :\n\n" +
                            "Name:"+ currentCompanyInfo.getName()+"\n" +
                            "Logo:"+ currentCompanyInfo.getLogo() +" \n" +
                            "Address:"+ currentCompanyInfo.getAddres() +"\n" +
                            "Company Type:"+Arrays.toString(token.getRoles()));

                    bot.sendMessage(chatId, companyInfo);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleEditInfo(String messageText, Long chatId, MyTelegramBot bot,String token) {
        String[] parts = messageText.split("-", 2);
        if (parts.length < 2) {
            bot.sendMessage(chatId, "Formato no válido.");
            return;
        }


        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/api/companies/getInfoCompanies";



            if (token == null || token.isEmpty()) {
                bot.sendMessage(chatId, "error: no se encontro un token de autenticacion.inicia sesion primero");
                return;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Companies> responseGet = restTemplate.exchange(url, HttpMethod.GET, request, Companies.class);

            if (responseGet.getStatusCode() == HttpStatus.OK) {
                Companies currentCompanyInfo = responseGet.getBody();
                if (currentCompanyInfo != null) {
                    String nuevaInfo=parts[1];
                    if (messageText.startsWith("/editAddress")){
                        currentCompanyInfo.setAddres(nuevaInfo);
                    } else if (messageText.startsWith("/editLogo")) {
                        currentCompanyInfo.setLogo(nuevaInfo);
                    } else if (messageText.startsWith("/editName")) {
                        currentCompanyInfo.setName(nuevaInfo);
                    }

                    String urlPost = "http://localhost:8080/api/companies/editInformation/"+currentCompanyInfo.getId();

                    HttpEntity<Companies> postRequest = new HttpEntity<>(currentCompanyInfo, headers);

                    ResponseEntity<String> responsePost = restTemplate.exchange(urlPost, HttpMethod.PUT, postRequest, String.class);
                    if (responsePost.getStatusCode() == HttpStatus.OK) {
                        bot.sendMessage(chatId, "inofrmacion de compañia guardada con exito");
                    } else {
                        bot.sendMessage(chatId, "Error al guardar la información de la compañía: " + responsePost.getStatusCode());
                    }
                } else {
                    bot.sendMessage(chatId, "No se encontró la información actual de la compañía.");
                }

            } else {
                bot.sendMessage(chatId, "Error al obtener la información de la compañía: " + responseGet.getStatusCode());
            }

        } catch (Exception e) {
            bot.sendMessage(chatId, "Error al hacer el cambio de información: " + e.getMessage());
        }
    }
}
