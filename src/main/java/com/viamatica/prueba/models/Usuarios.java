package com.viamatica.prueba.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuarios implements UserDetails{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    @Column
    
    private String username;
    @Column
    private String password;
    @Column
    private String mail;
    @Column
    private String sessionActive;
    @OneToOne
    private Persona persona;
    @Column
    private String status;

    @Column
    private int intentosLogin;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name= "usuarios_rol",
        joinColumns= @JoinColumn(name="usuario_id"),
        inverseJoinColumns= @JoinColumn(name= "rol_id")
    )
    private List<Rol> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       List<GrantedAuthority> authorities = new ArrayList<>();
        
        for (Rol rol : roles) {
            authorities.add(new SimpleGrantedAuthority(rol.getRolName()));
        }
        
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void crearMail(Long cantidad)
    {
        char primeraLetraNombre = persona.getNombres().toCharArray()[0];
        char primeraLetraSegundoApellido = persona.getApellidos().toCharArray()[0];
        String primerApellido = persona.getApellidos().split(" ")[1];
        String correo = "";
        if(cantidad == 0){
            correo = primeraLetraNombre + primerApellido + primeraLetraSegundoApellido + "@mail.com";
        }else{
            correo = primeraLetraNombre + primerApellido + primeraLetraSegundoApellido+ cantidad + "@mail.com";
        }
        mail = correo.toLowerCase();
    }

    public String getMailGeneratedString()
    {
        char primeraLetraNombre = persona.getNombres().toCharArray()[0];
        char primeraLetraSegundoApellido = persona.getApellidos().toCharArray()[0];
        String primerApellido = persona.getApellidos().split(" ")[1];
        String correo = primeraLetraNombre + primerApellido + primeraLetraSegundoApellido;
        
        return correo.toLowerCase();
    }
}
