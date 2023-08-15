package com.viamatica.prueba.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.viamatica.prueba.models.Usuarios;

import java.util.Optional;


public interface UsuariosRepository extends JpaRepository<Usuarios, Long>{
    Optional<Usuarios> findByIdUsuario(Long idUsuario);
    Optional<Usuarios> findByUsername(String username);
    @Query("SELECT COUNT(u) FROM Usuarios u WHERE u.mail LIKE %:email%")
    Long countUsersWithEmailLike(@Param("email") String email);
    
}
