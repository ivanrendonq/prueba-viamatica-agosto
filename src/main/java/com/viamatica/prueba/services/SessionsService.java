package com.viamatica.prueba.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viamatica.prueba.models.Sessions;
import com.viamatica.prueba.repositories.SessionsRepository;

@Service
public class SessionsService {
    
    @Autowired
    private SessionsRepository sessionsRepository;

    // @Autowired
    // private UsuariosRepository usuariosRepository;

    public Sessions crearSession(Sessions sessions) {
        return sessionsRepository.save(sessions);
    }

    public Sessions cerrarSession(String jwt) throws Exception{
        var session = sessionsRepository.findByToken(jwt).orElseThrow();
        if(session.getFechaCierra() == null)
        {
            session.setFechaCierra(LocalDateTime.now());
            sessionsRepository.save(session);
            return session;
        }else{
            throw new Exception("Token no valido, ya fue expirado el " + session.getFechaCierra());
        }
    }

    public Optional<Sessions> findByToken(String token) {
        var t = sessionsRepository.findByToken(token);
        return t;
    }

    

}
