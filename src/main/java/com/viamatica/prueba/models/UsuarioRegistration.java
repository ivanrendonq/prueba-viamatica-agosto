package com.viamatica.prueba.models;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioRegistration {
    @Pattern(regexp="^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,20}$")
    private String username;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?!.*\\s).{8,}$")
    private String password;
    private String nombres;
    private String apellidos;
    @Pattern(regexp = "^(?!.*([0-9])\\1{3})([0-9]){10}$")
    private String identificacion;
    private LocalDateTime fechaNacimiento;

}
