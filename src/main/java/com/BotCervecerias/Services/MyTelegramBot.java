package com.BotCervecerias.Services;

import com.BotCervecerias.CommandsTG.*;
import com.BotCervecerias.Config.Config;
import com.BotCervecerias.Response.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    @Autowired
    private final UserCommands userCommands=new UserCommands() ;

    private final LoginCommands loginCommands;
    @Autowired
    private final EventsCommands eventsCommands=new EventsCommands();
    @Autowired
    private final BeersCommands beersCommands =new BeersCommands();

    @Autowired
    private final CompanyCommands companyCommands=new CompanyCommands();

    private Map<Long, String> userState = new HashMap<>();

    @Autowired
    public MyTelegramBot(LoginCommands loginCommands){
        this.loginCommands=loginCommands;
    }
    @Override
    public String getBotUsername() {
        return Config.BOT_NAME.getToken();
    }

    @Override
    public String getBotToken() {
        return Config.TELEGRAM_TOKEN.getToken();
    }

    JwtResponse response;// Almacenar el token JWT
    String dato;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();


            if (userState.containsKey(chatId)) {
                String state = userState.get(chatId);

                if (state.equals("register")) {
                    userCommands.handleRegistration(messageText, chatId, this);
                    userState.remove(chatId);

                } else if (state.equals("EventRegister")) {
                    eventsCommands.handleEventsRegistration(messageText,chatId,this, response.getToken());
                    userState.remove(chatId);

                } else if (state.equals("Beerregister")) {
                    beersCommands.handleBeerRegistration(messageText, chatId, this,response.getToken());
                    userState.remove(chatId);

                } else if (state.equals("login")) {
                    response=loginCommands.handleLogin(messageText, chatId, this);
                    userState.remove(chatId);

                }else if (state.equals("editInfo")){
                    companyCommands.handleEditInfo(messageText,chatId,this, response.getToken());
                    userState.remove(chatId);

                } else if (state.equals("EditEvent")) {
                    eventsCommands.handleEditEvent(dato,messageText,chatId,this,response.getToken());
                    userState.remove(chatId);

                } else if (state.equals("EditInfoUser")) {
                    userCommands.handleChangeInfoUser(messageText,chatId,this,response.getToken());
                    userState.remove(chatId);

                }else if (state.equals("EditInfoBeer")){
                    beersCommands.handleUpdateInfoBeer(dato,messageText,chatId,this, response.getToken());
                    userState.remove(chatId);

                } else if (state.equals("DeleteUser")) {
                    companyCommands.handleDeleteCompany(messageText,chatId,this, response.getToken());
                    userState.remove(chatId);

                }else if (state.equals("DeleteBeer")){
                    beersCommands.handleDeleteBeer(messageText,chatId,this,response.getToken());
                    userState.remove(chatId);

                }else if (state.equals("DeleteEvent")){
                    eventsCommands.handleDeleteEvent(messageText,chatId,this,response.getToken());
                    userState.remove(chatId);

                    //} else if (state.equals("VerInfo")) {
                    //companyCommands.handleGetInfoCompany(chatId,this,response);
                    //userState.remove(chatId);
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

                } else if (messageText.startsWith("/EventRegister")) {
                    sendMessage(chatId, """
                            ingrese los siguientes datos :
                            nombre del evento
                            direccion del evento
                            fecha del eventp
                            cervecerias que participan en el evento [formato: cerveceria1,cerveceria2...]
                            EJEMPLO: nombre del evento-direccion del evento-fecha del evento-cerveceria1,cerveceria2,...""");
                    userState.put(chatId,"EventRegister");

                } else if (messageText.startsWith("/login")) {
                        sendMessage(chatId, """
                                ingrese sus credenciales:
                                correo-contraseña""");

                        userState.put(chatId,"login");


                } else if (messageText.startsWith("/EditInfoUser")) {
                    sendMessage(chatId, """
                            Ingrese el comando correspondiente con la nueva informacion:
                            /editName-[nuevo nombre de usuario] 
                            /editPassword-[nueva contraseña]
                            ejemplo: /editName-Nuevo nombre de ususario""");
                    userState.put(chatId,"EditInfoUser");

                }else if (messageText.startsWith("/EditInfoCompany")){
                    sendMessage(chatId, """
                            /editAddress-[nueva direccion]
                            /editLogo-[url del nuevo logo]
                            /editName-[nuevo nombre]
                            Ejemplo: /editLogo-urlNuevologo""");
                    userState.put(chatId,"editInfo");

                } else if (messageText.startsWith("/EditEvent")) {
                    dato = messageText.split("-")[1];
                    sendMessage(chatId, """
                            ingrese el comando correspondiente con la nueva informacion:
                            /editName-[Nuevo Nombre]
                            /aggBrewery-[Nombre de la cerveceria]
                            /deleteBrewery-[Nombre de la cerveceria]
                            /editDate-[Nueva fecha (dd/mm/yy]
                            /editDirection-[Nueva direccion]
                            /changeStatus-[ACTIVO/INACTIVO]
                            ejemplo: /addBrewery-Cerveceria""");

                    userState.put(chatId,"EditEvent");

                } else if (messageText.startsWith("/EditInfoBeer")) {//el comando va seguido del nombre de la cerveza que se uiere editar
                    dato = messageText.split("-")[1];
                    sendMessage(chatId,"""
                            /editName-[nombre de la cerveza]
                            /editDescription-[nueva descripcion]
                            /editImage-[nuevo url]
                            /editType-[tipo de cerveza (TIPO1/TIPO2)]
                            ejemplo: /editDescription-Nueva descripcion del evento""");
                    userState.put(chatId,"EditInfoBeer");


                } else if (messageText.startsWith("/DeleteUser")) {
                    sendMessage(chatId, """
                            Al eliminar el usuario elimina todo registro de:
                            Cervezas y participación de eventos(en caso de ser CERVECERIA )
                            Eventos (en caso de ser una ORGANIZACION)
                            Envie 'YES' para confirmar y 'NO' para cancelar\s""");
                    userState.put(chatId,"DeleteUser");

                } else if (messageText.startsWith("/DeleteBeer")) {
                    sendMessage(chatId,"Seguro quiere eliminar una cerveza del registro?\n"+"En caso de estar segur@ ingrese el nombre de la cerveza a eliminar, caso contrario ingrese la palabra 'NO' para cancelar la solicitud");
                    userState.put(chatId,"DeleteBeer");

                } else if (messageText.startsWith("/DeleteEvent")) {
                    sendMessage(chatId,"Seguro quiere eliminar un evento del registro?\n"+"En caso de estar segur@, ingrese el nombre de la cerveza a eliminar, caso contrario ingrese la palabra 'NO' para cancelar la solicitud");
                    userState.put(chatId,"DeleteEvent");


                } else if (messageText.startsWith("/VerInfoCompany")) {
                    //sendMessage(chatId,"Obteniendo datos de la compañia...");
                    companyCommands.handleGetInfoCompany(chatId,this,response);
                    //userState.put(chatId,"VerInfo");

                } else if (messageText.startsWith("/VerInfoCerveza")) {
                    beersCommands.handleVerInfoCerveza(messageText,chatId,this,response.getToken());

                }else if (messageText.startsWith("/AllBeers")){
                    beersCommands.handleGetAllBeers(chatId,this, response.getToken());


                }else if (messageText.startsWith("/InfoEvent")){
                    eventsCommands.handleInfoEvent(messageText,chatId,this, response.getToken());

                } else if (messageText.startsWith("/InfoAllEvents")) {
                    eventsCommands.handleInfoAllEvents(chatId,this, response.getToken());
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
