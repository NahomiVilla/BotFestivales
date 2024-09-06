package com.BotCervecerias.Controllers;

import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Repositories.UserRepository;
import com.BotCervecerias.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users users){
        try {
            Users newUser=userService.registerUser(users);
            return ResponseEntity.ok(newUser);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error: "+e.getMessage());
        }
    }
}
