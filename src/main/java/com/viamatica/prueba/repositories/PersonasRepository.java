package com.viamatica.prueba.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viamatica.prueba.models.Persona;

import java.util.Optional;


public interface PersonasRepository extends JpaRepository<Persona, Long>{
    Optional<Persona> findByIdentificacion(String identificacion);
}
