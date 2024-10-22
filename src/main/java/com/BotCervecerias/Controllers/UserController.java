package com.BotCervecerias.Controllers;

import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Repositories.UserRepository;
import com.BotCervecerias.Services.UserService;
import com.BotCervecerias.Services.UserServiceImpl;
import com.BotCervecerias.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;

    @PutMapping("/editInfo/{id}")
    public ResponseEntity<Users> editInfoUser(@PathVariable Long id, Users nuevaInfo) throws IllegalAccessException {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        Optional<Users>userModel=userRepository.findByEmail(authentication.getName());
        Users users;
        if (userModel.isPresent()){
            Long idUser=userModel.get().getId();
             users=userService.updateInfo(idUser,nuevaInfo);
        }else {
            return ResponseEntity.badRequest().body(null);
        }return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @GetMapping("/InfoUser")
    public ResponseEntity<Users> getInfoUser(@RequestHeader("Authorization")String token){
        try{
            String jwtToken=token.replace("Bearer","");
            String email=jwtUtils.getEmailToken(jwtToken);
            Optional<Users>userModel=userRepository.findByEmail(email);
            Users users;
            if (userModel.isPresent()){
                users=userModel.get();
                return ResponseEntity.ok(users);
            }return ResponseEntity.badRequest().body(null);

        } catch (Exception e) {
            throw null;
        }
    }
}
