package com.BotCervecerias.CommandsTG;

import com.BotCervecerias.Models.Companies;
import com.BotCervecerias.Models.Events;
import com.BotCervecerias.Response.JwtResponse;
import com.BotCervecerias.Services.MyTelegramBot;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class EventsCommands {

    @Autowired
    private JwtResponse jwtResponse;

    public void handleEventsRegistration(String messageText, Long chatId, MyTelegramBot bot,String token){
        String[] parts = messageText.split("-", 6); // Limitar la división para que la descripción y la URL de imagen puedan contener espacios

        if (parts.length < 5) {
            bot.sendMessage(chatId, "Formato no válido. Usa: Nombre-Tipo-GradoAlcohol-BTU-Descripción-URLImagen");
            return;
        }

        String nombre=parts[0].toUpperCase();
        String lugar_evento=parts[1];
        String fechaStr=parts[2];
        String descripcion=parts[3];
        String cervecerias=parts[4];
        SimpleDateFormat formato=new SimpleDateFormat("yyy/MM/dd");
        Date eventDate;
        try{
             eventDate = formato.parse(fechaStr);  // Convierte el String a Date
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        List<String> breweryNames= Arrays.stream(cervecerias.split(","))
                .map(String::trim)
                .toList();

        if (token==null||token.isEmpty()){
            bot.sendMessage(chatId,"Error: no se encontró token de autenticacion");
            return;
        }

        try{
            Events event=new Events();
            event.setName(nombre);
            event.setDirection(lugar_evento);
            event.setDate(eventDate);
            event.setDescription(descripcion);

            RestTemplate restTemplate=new RestTemplate();
            String urlFind="http://localhost:8080/api/companies/findByNames";
            HttpHeaders headers=new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<List<String>> request=new HttpEntity<>(breweryNames,headers);

            ResponseEntity<Companies[]> response=restTemplate.postForEntity(urlFind,request,Companies[].class);
            if(response.getStatusCode()== HttpStatus.OK){
                Companies[] companies=response.getBody();
                if (companies==null||companies.length==0){
                    bot.sendMessage(chatId,"no se encontraron las cervecerias con os nombres proporcionados");
                    return;
                }
                event.setBreweries(Arrays.asList(companies));

                String urRegister="http://localhost:8080/api/events/register";
                HttpEntity<Events> eventRequest=new HttpEntity<>(event,headers);
                ResponseEntity<String> eventResponse=restTemplate.postForEntity(urRegister,eventRequest,String.class);

                if (eventResponse.getStatusCode() == HttpStatus.OK) {
                    String eventInfo="Evento registrado con éxito:\n\n" +
                                    "Nombre:"+ nombre.toUpperCase() +"\n" +
                                    "Lugar:"+ lugar_evento.toUpperCase() +" \n" +
                                    "Fecha:"+ eventDate +"\n" +
                                    "Descripción:"+ descripcion.toUpperCase() +"\n" +
                                    "Cervecerías participantes:"+cervecerias.toUpperCase();

                    bot.sendMessage(chatId, eventInfo);

                } else {
                    bot.sendMessage(chatId, "Error al registrar el evento: " + eventResponse.getStatusCode());
                }
            }else {
                bot.sendMessage(chatId, "Error al buscar las cervecerías: " + response.getStatusCode());
            }
        }catch (Exception e) {
            bot.sendMessage(chatId, "Error al procesar el registro del evento: " + e.getMessage());
        }


    }



    public void handleInfoEvent(String messageText,Long chatId,MyTelegramBot bot,String token){
        try{
            String nombre=messageText.split("-")[1].toUpperCase();
            RestTemplate restTemplate=new RestTemplate();
            String url="http://localhost:8080/api/events/getInfoEvent";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> request = new HttpEntity<>(nombre,headers);
            ResponseEntity<Events> responsePost = restTemplate.exchange(url, HttpMethod.POST, request, Events.class);

            if (responsePost.getStatusCode() == HttpStatus.OK) {
                Events currentEventInfo = responsePost.getBody();
                if (currentEventInfo != null) {
                    String active="";
                    if (currentEventInfo.getActive().equals(true)) {
                         active = "ACTIVO";
                    }else if(currentEventInfo.getActive().equals(false)){ active = "INACTIVO";}else if(currentEventInfo.getActive().equals(null)){active="Sin Informacion";}
                    String eventInfo="Evento :\n\n" +
                            "Nombre:"+ currentEventInfo.getName().toUpperCase() +"\n" +
                            "Lugar:"+ currentEventInfo.getDirection().toUpperCase() +" \n" +
                            "Fecha:"+ currentEventInfo.getDate() +"\n" +
                            "Descripción:"+ currentEventInfo.getDescription().toUpperCase() +"\n" +
                            "Estado: "+active+"\n"+
                            "Cervecerías participantes:"+currentEventInfo.getBreweriesNames();

                    bot.sendMessage(chatId, eventInfo);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleInfoAllEvents(Long chatId,MyTelegramBot bot,String token){
        try{
            RestTemplate restTemplate=new RestTemplate();
            String url="http://localhost:8080/api/events/getInfoAllEvents";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            System.out.println("haciendo la peticion");
            ResponseEntity<Events[]> responseGet = restTemplate.exchange(url, HttpMethod.GET, request, Events[].class);

            if (responseGet.getStatusCode() == HttpStatus.OK) {
                Events[] currentEventsList= responseGet.getBody();
                if (currentEventsList != null) {
                    for (Events event:currentEventsList){
                        String active="";
                        if (event.getActive().equals(true)) {
                            active = "ACTIVO";
                        }else if(event.getActive().equals(false)){ active = "INACTIVO";}else if(event.getActive().equals(null)){active="Sin Informacion";}

                        String eventInfo="Evento :\n\n" +
                                "Nombre:"+ event.getName().toUpperCase() +"\n" +
                                "Lugar:"+ event.getDirection().toUpperCase() +" \n" +
                                "Fecha:"+ event.getDate() +"\n" +
                                "Descripción:"+ event.getDescription().toUpperCase() +"\n" +
                                "Estado: "+active+"\n"+
                                "Cervecerías participantes:"+event.getBreweriesNames();
                        bot.sendMessage(chatId, eventInfo);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleDeleteEvent(String messageText,Long chatId,MyTelegramBot bot,String token){
        if (!messageText.equalsIgnoreCase("NO")) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                String urlDelete = "http://localhost:8080/api/events/deleteEvent";
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlDelete)
                        .queryParam("name", messageText.toUpperCase());
                String uri=builder.toUriString();
                HttpEntity<Companies> deleteRequest = new HttpEntity<>(headers);

                ResponseEntity<String> responseDelete = restTemplate.exchange(uri, HttpMethod.DELETE, deleteRequest, String.class);
                if (responseDelete.getStatusCode() == HttpStatus.OK) {
                    bot.sendMessage(chatId, "evento eliminado con exito");
                } else {
                    bot.sendMessage(chatId, "Error al eliminar el evento: " + responseDelete.getStatusCode());
                }

            } catch (Exception e) {
                bot.sendMessage(chatId, "Error al eliminar evento: " + e.getMessage());
            }
        } else {
            bot.sendMessage(chatId, "Accion cancelada");
        }
    }


    public void handleEditEvent(String dato,String messageText, Long chatId, MyTelegramBot bot,String token){
        String[] parts = messageText.split("-", 2); // Limitar la división para que la descripción y la URL de imagen puedan contener espacios

        if (parts.length < 2) {
            bot.sendMessage(chatId, "Formato no válido.");
            return;
        }

        String nuevaInfo=parts[1].toUpperCase();

        if (token==null||token.isEmpty()){
            bot.sendMessage(chatId,"Error: no se encontró token de autenticacion");
            return;
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/api/events/getInfoEvent";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> request = new HttpEntity<>(dato.toUpperCase(), headers);

            ResponseEntity<Events> response = restTemplate.postForEntity(url, request, Events.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Events evento = response.getBody();
                if (evento != null) {

                    if (messageText.startsWith("/editName")) {
                        evento.setName(nuevaInfo);
                        handleUpdateEvent(evento, headers, restTemplate, bot, chatId);

                    } else if (messageText.startsWith("/editDate")) {
                        SimpleDateFormat formato = new SimpleDateFormat("yyy/MM/dd");
                        Date eventDate;
                        eventDate = formato.parse(nuevaInfo);
                        evento.setDate(eventDate);
                        handleUpdateEvent(evento, headers, restTemplate, bot, chatId);
                    } else if (messageText.startsWith("/editDirection")) {
                        evento.setDirection(nuevaInfo);
                        handleUpdateEvent(evento, headers, restTemplate, bot, chatId);
                    } else if (messageText.startsWith("/changeStatus")) {
                        handleChangeStatus(nuevaInfo, evento, headers, restTemplate, bot, chatId);

                    } else if (messageText.startsWith("/aggBrewery")) {
                        handleAggBrewery(nuevaInfo, evento, headers, restTemplate, bot, chatId);


                    } else if (messageText.startsWith("/deleteBrewery")) {
                        System.out.println(evento);
                        System.out.println("Mensaje recibido: " + messageText);
                        handleDeleteBrewery(nuevaInfo, evento, headers, restTemplate, bot, chatId);
                    }
                }
            }
            //return;
        }catch (Exception e){
            bot.sendMessage(chatId, "Error al procesar la solicitud de cambio: " + e.getMessage());

        }
    }


    private void handleUpdateEvent(Events evento,HttpHeaders headers,RestTemplate restTemplate,MyTelegramBot bot,Long chatId){
        String urlChange = "http://localhost:8080/api/events/editEvent/"+evento.getId();

        HttpEntity<Events> eventRequest = new HttpEntity<>(evento, headers);
        ResponseEntity<Events> eventResponse = restTemplate.exchange(urlChange, HttpMethod.PUT, eventRequest, Events.class);

        if (eventResponse.getStatusCode() == HttpStatus.OK) {
            bot.sendMessage(chatId, "Se cambió la inforamación del evento con exito");
        }
    }

    private void handleDeleteBrewery(String nuevaInfo,Events evento,HttpHeaders headers,RestTemplate restTemplate,MyTelegramBot bot,Long chatId){
        System.out.println("obteniendo info de la cerveceria");
        String urlFind = "http://localhost:8080/api/companies/findByNames";

        HttpEntity<List<String>> requestFind = new HttpEntity<>(List.of(nuevaInfo), headers);

        ResponseEntity<Companies[]> responseFind = restTemplate.postForEntity(urlFind, requestFind, Companies[].class);
        if (responseFind.getStatusCode() == HttpStatus.OK) {
            Companies[] companies = responseFind.getBody();
            if (companies == null || companies.length == 0) {
                bot.sendMessage(chatId, "no se encontraron las cervecerias con os nombres proporcionados");
                return;
            }

            Companies company = companies[0];
            System.out.println(company);
            System.out.println(evento);
            Long companyId = company.getId();
            System.out.println("eliminando cerveceria");
            String urlDelete = "http://localhost:8080/api/events/deleteBrewery/" + companyId;
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlDelete)
                    .queryParam("eventId", evento.getId());
            String uri = builder.toUriString();
            HttpEntity<Void> deleteRequest = new HttpEntity<>(headers);
            ResponseEntity<String> deleteResponse = restTemplate.exchange(uri, HttpMethod.DELETE, deleteRequest, String.class);
            if (deleteResponse.getStatusCode() == HttpStatus.OK) {
                bot.sendMessage(chatId, "cerveceria eliminada del evento");
            } else {
                bot.sendMessage(chatId, "erros al eliminar la cerveceria");
            }

        }
    }

    private void handleAggBrewery(String nuevaInfo,Events evento,HttpHeaders headers,RestTemplate restTemplate,MyTelegramBot bot,Long chatId){
        String urlFind="http://localhost:8080/api/companies/findByName";

        HttpEntity<String> requestFind=new HttpEntity<>(nuevaInfo,headers);

        ResponseEntity<Companies> responseFind=restTemplate.postForEntity(urlFind,requestFind,Companies.class);
        if(responseFind.getStatusCode()== HttpStatus.OK){
            Companies companies=responseFind.getBody();
            if (companies==null){
                bot.sendMessage(chatId,"no se encontraron las cervecerias con os nombres proporcionados");
                return;
            }

            System.out.println("compañia a agregar: "+companies);

            String urlAgg="http://localhost:8080/api/events/aggBrewery/"+evento.getId();

            HttpEntity<Companies>putRequest=new HttpEntity<>(companies,headers);
            ResponseEntity<String>putResponse=restTemplate.exchange(urlAgg,HttpMethod.PUT,putRequest,String.class);
            if (putResponse.getStatusCode()==HttpStatus.OK){
                bot.sendMessage(chatId,"cerveceria agregada");
            }else {
                bot.sendMessage(chatId,"erros al agregar la cerveceria");
            }
        }
    }

    private void handleChangeStatus(String nuevaInfo,Events evento,HttpHeaders headers,RestTemplate restTemplate,MyTelegramBot bot,Long chatId){

        String urlStatus = "http://localhost:8080/api/events/" + evento.getId() + "/status";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlStatus);
        if (nuevaInfo.equalsIgnoreCase("ACTIVO")) {
            builder .queryParam("status", true);
        } else if (nuevaInfo.equalsIgnoreCase("INACTIVO")) {
            builder.queryParam("status", false);
        }else{
            bot.sendMessage(chatId,"ingreso no valido");
            return;
        }
        String uri=builder.toUriString();

        HttpEntity<String> statusRequest = new HttpEntity<>(null, headers);
        ResponseEntity<String> statusResponse = restTemplate.exchange(uri, HttpMethod.PUT, statusRequest, String.class);

        if (statusResponse.getStatusCode() == HttpStatus.OK) {
            bot.sendMessage(chatId, "Se cambió el estado del evento con exito");
        }
    }
}


