package com.krakedev.taller.jwt.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.taller.jwt.entidades.Usuario;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Generar token JWT
    public String generarToken(Usuario usuario) {

        Algorithm algorithm = Algorithm.HMAC256(secret);

        Date fechaActual = new Date();
        Date fechaExpiracion = new Date(fechaActual.getTime() + expiration);

        return JWT.create()
                .withSubject(usuario.getUsername())
                .withClaim("rol", usuario.getRol())
                .withIssuedAt(fechaActual)
                .withExpiresAt(fechaExpiracion)
                .sign(algorithm);
    }

    // Validar token JWT
    public DecodedJWT validarToken(String token) {

        try {

            Algorithm algorithm = Algorithm.HMAC256(secret);

            JWTVerifier verifier = JWT.require(algorithm).build();

            return verifier.verify(token);

        } catch (JWTVerificationException e) {

            System.out.println("ERROR AL VALIDAR JWT: " + e.getMessage());

            throw new RuntimeException("Token invalido o expirado");
        }
    }
}
