package com.BotCervecerias.security.jwt;

import com.BotCervecerias.Models.Users;
import com.BotCervecerias.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {
    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.time.expiration}")
    private String timeExpiration;

    @Autowired
    private UserRepository userRepository;

    //Generar un token de acceso
    public String generateAccessToken(String email) {
        Users userModel = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

        Hibernate.initialize(userModel.getUsersType());


        Long idType = userModel.getUsersType().getId();
        String type = userModel.getUsersType().getName();
        return Jwts.builder()
                .setSubject(email)
                .claim("idType", idType)
                .claim("type", type)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Validar token de acceso
    public boolean isTokenValid(String token) {
        try{
            Jwts.parser()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    //Obtener el email del token
    public String getEmailToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    //Obtener el rol del token
    public String getUserRole(String token) {
        return getClaim(token, claims -> claims.get("type", String.class));
    }

    //Obtener un solo claim
    public <T> T getClaim(String token, Function<Claims, T> claimsFunction) {
        Claims claims = extractAllClaims(token);
        return claimsFunction.apply(claims);
    }

    //Obtener todos los claims del token
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Obtener firma de token
    public Key getSignatureKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}