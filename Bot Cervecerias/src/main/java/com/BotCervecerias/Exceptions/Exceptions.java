package com.BotCervecerias.Exceptions;
public class Exceptions {

    public static class EmailNotFoundException extends RuntimeException {
        public EmailNotFoundException(String message) {
            super(message);
        }
    }

}
