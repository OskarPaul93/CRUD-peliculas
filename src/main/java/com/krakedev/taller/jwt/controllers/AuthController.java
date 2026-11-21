package com.krakedev.taller.jwt.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.taller.jwt.entidades.Usuario;
import com.krakedev.taller.jwt.repositories.UsuarioRepository;
import com.krakedev.taller.jwt.utils.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // REGISTRAR USUARIO
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {

        try {

            if (usuario.getUsername() == null
                    || usuario.getUsername().isBlank()
                    || usuario.getPassword() == null
                    || usuario.getPassword().isBlank()) {

                return ResponseEntity.badRequest()
                        .body("El usuario y la contraseña son obligatorios");
            }

            if (usuarioRepository.existsByUsername(usuario.getUsername())) {
                return ResponseEntity.badRequest()
                        .body("El nombre de usuario ya existe");
            }

            if (usuario.getRol() == null
                    || (!usuario.getRol().equals("ADMIN")
                    && !usuario.getRol().equals("USER"))) {

                return ResponseEntity.badRequest()
                        .body("El rol debe ser ADMIN o USER");
            }

            usuario.setPassword(
                    passwordEncoder.encode(usuario.getPassword())
            );

            Usuario usuarioGuardado = usuarioRepository.save(usuario);

            usuarioGuardado.setPassword(null);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(usuarioGuardado);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el usuario: " + e.getMessage());
        }
    }

    // INICIAR SESION
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {

        try {

            Usuario usuarioEncontrado = usuarioRepository
                    .findByUsername(usuario.getUsername())
                    .orElse(null);

            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario o contraseña incorrectos");
            }

            if (!passwordEncoder.matches(
                    usuario.getPassword(),
                    usuarioEncontrado.getPassword())) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario o contraseña incorrectos");
            }

            String token = jwtUtil.generarToken(usuarioEncontrado);

            Map<String, Object> respuesta = new HashMap<>();

            respuesta.put("mensaje", "Inicio de sesión correcto");
            respuesta.put("username", usuarioEncontrado.getUsername());
            respuesta.put("rol", usuarioEncontrado.getRol());
            respuesta.put("token", token);
            respuesta.put("tipo", "Bearer");

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al iniciar sesión: " + e.getMessage());
        }
    }
}
