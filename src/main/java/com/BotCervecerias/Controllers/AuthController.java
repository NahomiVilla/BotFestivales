package com.BotCervecerias.Controllers;

import com.BotCervecerias.Models.Beers;
import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Services.BeerService;
import com.BotCervecerias.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private BeerService beerService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users users){
        try {
            Users newUser=userService.registerUser(users);
            return ResponseEntity.ok(newUser);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error: "+e.getMessage());
        }
    }
    @PostMapping("/BeerRegister")
    public ResponseEntity<?> registerBeer(@RequestBody Beers beers){
        try {
            Beers newBeer=beerService.registerBeer(beers);
            return ResponseEntity.ok(newBeer);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error: "+e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users userRequest, HttpServletRequest request) {
        try {
            String token = userService.authenticateUser(userRequest.getEmail(), userRequest.getPassword());
            return ResponseEntity.ok().header("Authorization", "Bearer " + token)
                    .body(Map.of(
                            "status", HttpStatus.OK.value(),
                            "token", token,
                            "message", "Autenticación correcta",
                            "username", userRequest.getEmail()
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
