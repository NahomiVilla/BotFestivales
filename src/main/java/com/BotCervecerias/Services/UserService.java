package com.BotCervecerias.Services;

import com.BotCervecerias.Models.Users;
import org.springframework.stereotype.Service;


public interface UserService {
   Users registerUser (Users users);
   String loginUser(String email, String password);
   Users updateInfo(Long id,Users nuevaInfo) throws IllegalAccessException;
   }
