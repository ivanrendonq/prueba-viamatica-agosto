package com.viamatica.prueba.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolOpciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOpcion;
    @Column
    private String nombreOpcion;

    @ManyToMany(mappedBy = "opciones", fetch = FetchType.EAGER)
    private List<Rol> roles;
}
