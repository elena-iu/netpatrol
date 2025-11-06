package com.ghostnet.store.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ghostnet.store.domain.User;
import com.ghostnet.store.repository.UserRepository;

import java.io.IOException;

/**
 * Controller für User-Registrierung
 * Stellt Signup-Template bereit und verarbeitet Registrierungsanfragen
 */
@SuppressWarnings("null")
@RestController
public class SignupController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Zeigt die Registrierungsseite an
     * @return signup.html Template
     */
    @GetMapping("/signup.html")
    public ResponseEntity<Resource> signupPage() throws IOException {
        Resource resource = new ClassPathResource("templates/signup.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    /**
     * Erstellt einen neuen User
     * Verschlüsselt das Passwort vor dem Speichern
     * @param user User-Objekt mit Registrierungsdaten
     * @return Gespeicherter User
     */
    @PostMapping(value = "/req/signup", consumes = "application/json")
    public User createUser(@RequestBody User user) {
        // Passwort vor dem Speichern verschlüsseln
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
