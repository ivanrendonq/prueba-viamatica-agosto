package com.viamatica.prueba.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sessions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSession;
    @Column
    private LocalDateTime fechaIngreso;
    @Column
    private LocalDateTime fechaCierra;
    
    @Column 
    private String token;


    @ManyToOne(fetch = FetchType.EAGER)
    private Usuarios usuario;
}
