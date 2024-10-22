package com.BotCervecerias.Services;

import com.BotCervecerias.Exceptions.Exceptions;
import com.BotCervecerias.Models.Companies;
import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Models.UsersType;
import com.BotCervecerias.Repositories.CompaniesRepository;
import com.BotCervecerias.Repositories.UserRepository;
import com.BotCervecerias.Repositories.UsersTypeRepository;
import com.BotCervecerias.security.jwt.JwtUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private UsersTypeRepository usersTypeRepository;
    @Autowired
    private CompaniesRepository companiesRepository;
    public Users registerUser(Users users){
        if (userRepository.findByEmail(users.getEmail()).isPresent()){
            throw  new RuntimeException("el correo ya está en uso.");
        }
        String encodePassword=passwordEncoder.encode(users.getPassword());
        UsersType usersType=usersTypeRepository.findById(users.getUsersTypeId()).orElseThrow(()->new Exceptions.UserTypeNotFoundException("Tipo de usuario no encontrado"));
        Users newuser= Users.builder()
                        .name(users.getName())
                        .email(users.getEmail())
                        .password(encodePassword)
                        .usersType(usersType)
                        .build();
        userRepository.save(newuser);
        Companies newCompany=Companies.builder()
                .name(newuser.getName())
                .addres("Address default")
                .logo("url logo")
                .users(newuser)
                .build();
        companiesRepository.save(newCompany);

        return newuser;
    }
    public String loginUser(String email, String password) {
        // Autenticación del usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtUtils.generateAccessToken(authentication.getName());
        return jwtToken;
    }

    public Users updateInfo(Long id,Users nuevaInfo)throws  IllegalAccessException{
        Users users=userRepository.findById(id).orElseThrow(()->new IllegalAccessException("User not found for this is : "+id));
        users.setName(nuevaInfo.getName());
        String encodePassword=passwordEncoder.encode(nuevaInfo.getPassword());
        users.setPassword(encodePassword);
        return userRepository.save(users);
    }





}
