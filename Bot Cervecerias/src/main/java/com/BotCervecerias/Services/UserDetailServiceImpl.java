package com.BotCervecerias.Services;

import com.BotCervecerias.Exceptions.Exceptions;
import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;


@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByEmail(username);  // Delegate to loadUserByEmail
    }

    public UserDetails loadUserByEmail(String email) throws Exceptions.EmailNotFoundException {
        Users userModel = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exceptions.EmailNotFoundException("El email " + email + " no existe"));
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_".concat(userModel.getUsersType().getName()));
        Collection<? extends GrantedAuthority> authorities = Collections.singleton(authority);

        return new User(
                userModel.getEmail(),
                userModel.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }
}