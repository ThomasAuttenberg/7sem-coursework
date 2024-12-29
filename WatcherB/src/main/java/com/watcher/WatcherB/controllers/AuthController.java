package com.watcher.WatcherB.controllers;

import com.watcher.WatcherB.DTO.Auth.LoginRequest;
import com.watcher.WatcherB.DTO.Auth.LoginResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Аутентификация пользователя
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            String tokenDecrypted = loginRequest.getUsername() + ":" + loginRequest.getPassword(); // Пароль в открытом виде
            return ResponseEntity.status(200).body(
                    new LoginResponse("Авторизация пройдена",
                            authentication.getName(),
                            authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList(),
                            Base64.getEncoder().encodeToString(tokenDecrypted.getBytes())
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(new LoginResponse("Авторизация провалена", null, Collections.EMPTY_LIST, null));
        }
    }
}