package com.krakedev.taller.jwt.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.krakedev.taller.jwt.entidades.Pelicula;
import com.krakedev.taller.jwt.services.PeliculaService;

@RestController
@RequestMapping("/peliculas")
public class PeliculaController {

    private final PeliculaService peliculaService;

    public PeliculaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    // Consultar todas las peliculas
    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<Pelicula> peliculas = peliculaService.listar();
            return ResponseEntity.ok(peliculas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar las peliculas: " + e.getMessage());
        }
    }

    // Consultar una pelicula por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            Optional<Pelicula> pelicula = peliculaService.buscarPorId(id);

            if (pelicula.isPresent()) {
                return ResponseEntity.ok(pelicula.get());
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontro la pelicula con ID: " + id);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar la pelicula: " + e.getMessage());
        }
    }

    // Registrar una pelicula con su imagen
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> guardar(
            @RequestParam String titulo,
            @RequestParam String genero,
            @RequestParam String descripcion,
            @RequestParam MultipartFile imagen) {
        try {
            if (titulo.isBlank() || genero.isBlank() || descripcion.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("Todos los campos son obligatorios");
            }

            if (imagen.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Debe seleccionar una imagen");
            }

            Pelicula pelicula = new Pelicula();
            pelicula.setTitulo(titulo);
            pelicula.setGenero(genero);
            pelicula.setDescripcion(descripcion);
            pelicula.setImagen(imagen.getBytes());

            Pelicula guardada = peliculaService.guardar(pelicula);

            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar la pelicula: " + e.getMessage());
        }
    }

    // Actualizar una pelicula
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestParam String titulo,
            @RequestParam String genero,
            @RequestParam String descripcion,
            @RequestParam(required = false) MultipartFile imagen) {
        try {
            Optional<Pelicula> resultado = peliculaService.buscarPorId(id);

            if (resultado.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontro la pelicula con ID: " + id);
            }

            Pelicula pelicula = resultado.get();
            pelicula.setTitulo(titulo);
            pelicula.setGenero(genero);
            pelicula.setDescripcion(descripcion);

            if (imagen != null && !imagen.isEmpty()) {
                pelicula.setImagen(imagen.getBytes());
            }

            Pelicula actualizada = peliculaService.guardar(pelicula);

            return ResponseEntity.ok(actualizada);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la pelicula: " + e.getMessage());
        }
    }

    // Eliminar una pelicula
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            Optional<Pelicula> resultado = peliculaService.buscarPorId(id);

            if (resultado.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontro la pelicula con ID: " + id);
            }

            peliculaService.eliminar(id);

            return ResponseEntity.ok("Pelicula eliminada correctamente");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la pelicula: " + e.getMessage());
        }
    }
}
