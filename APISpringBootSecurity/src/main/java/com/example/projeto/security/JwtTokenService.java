package com.example.projeto.security;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.projeto.model.ModelUserDetailsImpl;

@Service
public class JwtTokenService {

    @Value("${token.jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expirationHours}")
    private int expirationHours;

    public String generateToken(ModelUserDetailsImpl user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.create()
                    .withIssuedAt(dataCriacao())
                    .withExpiresAt(dataExpiracao())
                    .withSubject(user.getUsername())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar o token: " + exception.getMessage(), exception);
        }
    }

public String pegarToken(String token) {
    try {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        DecodedJWT decodedJWT = JWT.require(algorithm)
                                   .build()
                                   .verify(token);
        return decodedJWT.getSubject(); // Extraia o sujeito (username ou id)
    } catch (JWTVerificationException e) {
        throw new RuntimeException("Token invalido ou expirado!", e);
    }
}


    private Instant dataExpiracao() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
                .plusHours(expirationHours).toInstant();
    }

    private Instant dataCriacao() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
    }
}
