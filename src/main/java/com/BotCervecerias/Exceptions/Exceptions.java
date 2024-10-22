package com.BotCervecerias.Exceptions;
public class Exceptions {

    public static class EmailNotFoundException extends RuntimeException {
        public EmailNotFoundException(String message) {
            super(message);
        }
    }
    public static class UserTypeNotFoundException extends RuntimeException {
        public UserTypeNotFoundException(String message) {
            super(message);
        }
    }

}
