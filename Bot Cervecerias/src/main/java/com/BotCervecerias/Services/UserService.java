package com.BotCervecerias.Services;

import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Repositories.UserRepository;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Users registerUser(Users users){
        if (userRepository.findByCorreo(users.getEmail()).isPresent()){
            throw  new RuntimeException("el correo ya está en uso.");
        }
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        return userRepository.save(users);
    }
}
