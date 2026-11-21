package com.krakedev.taller.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.krakedev.taller.jwt.entidades.Pelicula;

public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {

}
