package com.viamatica.prueba.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viamatica.prueba.models.Login;
import com.viamatica.prueba.models.Response;
import com.viamatica.prueba.models.UsuarioRegistration;
import com.viamatica.prueba.services.JwtService;
import com.viamatica.prueba.services.UsuariosService;

import jakarta.validation.Valid;

import org.springframework.security.core.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("api/usuarios")
public class UsuariosController {
    
    @Autowired
    private UsuariosService usuariosService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/registrar")
    public Response registarUsuario(@RequestBody @Valid UsuarioRegistration usuario, BindingResult bindingResult) {
        Response response;

        try{
            if(bindingResult.hasErrors()){
               
                response = Response.builder()
                    .code("01")
                    // .message("Revise los datos: " + msj)
                    .message(bindingResult.getAllErrors().toString())
                    .data(null)
                    .build();
                    return response;    
            }
            var usuarioRegister = usuariosService.registarUsuario(usuario);
            response = Response.builder()
                .code("00")
                .message("Se registró el usuario")
                .data(usuarioRegister)
                .build();
        }catch(Exception e) {
            response = Response.builder()
                .code("01")
                .message("NO se registró el usuario" + "\nError: " + e.getLocalizedMessage())
                .data(null)
                .build();
                
        }
        return response;
    }

    @PostMapping("/login")
    public Object login(@RequestBody Login login) throws AuthenticationException
    {
        Response response;
        try {
            response = Response.builder()
                .code("00")
                .message("Se hizo login correctamente")
                .data(null)
                .build();

                return ResponseEntity.ok().header("Authorization", "Bearer " + usuariosService.login(login)).body(response);
        }catch(AuthenticationException authenticationException){
            if(usuariosService.obteneUsuarioPorNombre(login.getUsername()) != null)
            {
                if(usuariosService.aumentarIntentos(login.getUsername()).getIntentosLogin() == 4)
                {
                    response = Response.builder()
                    .code("01")
                    .message("Cuenta bloqueada")
                    .data(null)
                    .build();
                }else {
                    response = Response.builder()
                    .code("00")
                    .message("Contraseña incorrecta")
                    .data(null)
                    .build();
                }
                
            }else{
                response = Response.builder()
                    .code("00")
                    .message("Usuario no existe")
                    .data(null)
                    .build();
            }
        }catch (Exception e) {
            response = Response.builder()
                .code("01")
                .message("No se pudo hacer el login, Error " + e.getMessage())
                .data(null)
                .build();
        }

        return response;
    }

    @PostMapping("/cerrar-sesion")
    public Object cerrarSession(@RequestHeader("Authorization") String token) throws Exception
    {
        token = jwtService.getTokenFromHeader(token);
        
        if(usuariosService.cerrarSession(token)){
            return Response.builder()
                .code("00")
                .message("Se cerró la sesion")
                .build();
        }else{
            return Response.builder()
                .code("01")
                .message("No se cerró la sesion")
                .build();
        }
    }

    @DeleteMapping("/{idUsuario}")
    public Response borrarCuenta(@PathVariable Long idUsuario)
    {
        Response response;
        try {

            if(usuariosService.borrarCuenta(idUsuario))
            {
                response = Response.builder()
                    .code("00")
                    .message("Se borró correctamente")
                    .data(null)
                    .build();
            }else{
                response = Response.builder()
                .code("01")
                .message("No existe el usuario para borrar")
                .data(null)
                .build();
            }
            

        } catch (Exception e) {
            response = Response.builder()
                .code("01")
                .message("Error " + e.getMessage())
                .data(null)
                .build();
        }

        return response;
    }

}
