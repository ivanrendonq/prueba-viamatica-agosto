package com.viamatica.prueba.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viamatica.prueba.models.Sessions;
import com.viamatica.prueba.models.Usuarios;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;


@Repository
public interface SessionsRepository extends JpaRepository<Sessions, Long>{
    Optional<Sessions> findByToken(String token);
    List<Sessions> findByUsuario(Usuarios usuario);
    List<Sessions> findByFechaIngreso(LocalDateTime fechaIngreso);
    List<Sessions> findByFechaCierra(LocalDateTime fechaCierra);
}
