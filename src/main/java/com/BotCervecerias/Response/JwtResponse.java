package com.BotCervecerias.Response;


import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtResponse {
    private String[] roles;
    private String message;
    private int status;
    private String token;
    private String username;

    public JwtResponse() {}


}
