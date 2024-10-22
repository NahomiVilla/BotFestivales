package com.BotCervecerias.Config;

import com.BotCervecerias.CommandsTG.LoginCommands;
import com.BotCervecerias.Services.MyTelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public enum Config {

     BOT_NAME("BreweriessBot"),

    TELEGRAM_TOKEN("7039313046:AAF2AvsR_JinSsRxrHty4oJDKgh21Q7u6cI");

    private final String token;


    Config(String info) {
        this.token = info;

    }
    public String getToken() {
        return token;
    }




}
