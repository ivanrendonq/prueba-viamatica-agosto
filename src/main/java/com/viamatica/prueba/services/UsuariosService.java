package com.viamatica.prueba.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.viamatica.prueba.models.Login;
import com.viamatica.prueba.models.Persona;
import com.viamatica.prueba.models.Rol;
import com.viamatica.prueba.models.RolOpciones;
import com.viamatica.prueba.models.Sessions;
import com.viamatica.prueba.models.UsuarioRegistration;
import com.viamatica.prueba.models.Usuarios;
import com.viamatica.prueba.repositories.PersonasRepository;
import com.viamatica.prueba.repositories.UsuariosRepository;
import org.springframework.security.core.AuthenticationException;

@Service
public class UsuariosService {
    
    @Autowired
    private PersonasRepository personasRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private SessionsService sessionsService;

    public Usuarios registarUsuario(UsuarioRegistration usuarioRegistration) throws Exception
    {
        if(usuariosRepository.findByUsername(usuarioRegistration.getUsername()).isPresent()){
            throw new Exception("Usuario ya existente");
        }

        var persona = personasRepository.findByIdentificacion(usuarioRegistration.getIdentificacion());
        var usuario = Usuarios.builder()
            .username(usuarioRegistration.getUsername())
            .password(passwordEncoder.encode( usuarioRegistration.getPassword())).build();

        if(persona.isEmpty())
        {
            usuario.setPersona( 
                personasRepository.save(
                Persona.builder().nombres(usuarioRegistration.getNombres())
                .apellidos(usuarioRegistration.getApellidos())
                .identificacion(usuarioRegistration.getIdentificacion())
                .fechaNacimiento(usuarioRegistration.getFechaNacimiento())
                .build()
            ));
        }else{
            usuario.setPersona(persona.get());
        }
        
        Long cantidadmail = usuariosRepository.countUsersWithEmailLike(usuario.getMailGeneratedString());

        usuario.crearMail(cantidadmail);
        usuario.setSessionActive("N");
        usuario.setStatus("A");

        var rol = Rol.builder().rolName("usuario").opciones(
            List.of(
                RolOpciones.builder().nombreOpcion("read").build()
            )
        ).build();

        usuario.setRoles(List.of(rol));
        return usuariosRepository.save(usuario);
    }

    public Usuarios obteneUsuarioPorNombre(String username)
    {
        var usuario = usuariosRepository.findByUsername(username);
        if(usuario.isPresent()){
            return usuario.get();
        }
        return null;
    }

    public Usuarios aumentarIntentos(String username)
    {
        var usuario = usuariosRepository.findByUsername(username);
        if(usuario.isPresent()){
            usuario.get().setIntentosLogin(usuario.get().getIntentosLogin() + 1);
            usuariosRepository.save(usuario.get());
            if(usuario.get().getIntentosLogin() > 3){
                usuario.get().setStatus("bloqueado");
            }
            return usuario.get();
        }
        return null;
    }

    public String login(Login loginData) throws AuthenticationException, Exception
    {
        //Obtener el usuario o lanza error
        var usuario = usuariosRepository.findByUsername(loginData.getUsername()).orElseThrow();
        
        //Si tiene mas de 3 intentos no deja seguir
        if(usuario.getIntentosLogin() > 3){
            throw new Exception("Cuenta bloqueada");
        }

        if(usuario.getSessionActive() == "A")
        {
            throw new Exception("Cuenta ya logueada");
        }

        //Authenticar, si algo está mal lanza error
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken( loginData.getUsername(), loginData.getPassword())
        );

        //Crea el token
        var jwtToken = jwtService.generateToken(usuario);

        sessionsService.crearSession( 
            Sessions.builder()
                .token(jwtToken)
                .fechaIngreso(LocalDateTime.now())
                .usuario(usuariosRepository.findByUsername(loginData.getUsername()).get())
                .build()
        );
        usuario.setSessionActive("A");
        usuariosRepository.save(usuario);
        return jwtToken;
    }

    public boolean cerrarSession(String token) throws Exception{
        var t = sessionsService.findByToken(token);
        if(t.isPresent())
        {
            sessionsService.cerrarSession(token);
            t.get().getUsuario().setSessionActive("N");
            usuariosRepository.save(t.get().getUsuario());
            return true;
        }else{
            throw new Error("No existe ese token");
        }
        
    }

    public boolean isValidToken(String token, Login loginData)
    {
        return jwtService.istokenValid(token, 
        Usuarios.builder().
            username(loginData.getUsername()).
            password(loginData.getPassword()).build());
    }

    public boolean borrarCuenta(Long idUsuario)
    {
        if(usuariosRepository.findById(idUsuario).isPresent()){
            usuariosRepository.deleteById(idUsuario);
            return true;
        }
        else{
            return false;
        }
    }
}
