package com.krakedev.taller.jwt.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.krakedev.taller.jwt.entidades.Pelicula;
import com.krakedev.taller.jwt.repositories.PeliculaRepository;

@Service
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;

    public PeliculaService(PeliculaRepository peliculaRepository) {
        this.peliculaRepository = peliculaRepository;
    }

    // Registrar o actualizar una pelicula
    public Pelicula guardar(Pelicula pelicula) {
        return peliculaRepository.save(pelicula);
    }

    // Consultar todas las peliculas
    public List<Pelicula> listar() {
        return peliculaRepository.findAll();
    }

    // Buscar una pelicula por su ID
    public Optional<Pelicula> buscarPorId(Integer id) {
        return peliculaRepository.findById(id);
    }

    // Eliminar una pelicula
    public void eliminar(Integer id) {
        peliculaRepository.deleteById(id);
    }
}

