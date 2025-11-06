package com.ghostnet.store.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * Controller für die Login-Seite
 * Stellt das Login-Template bereit
 */
@SuppressWarnings("null")
@Controller
public class LoginController {

    /**
     * Zeigt die Login-Seite an
     * @return login.html Template
     */
    @GetMapping("/login.html")
    public ResponseEntity<Resource> login() throws IOException {
        Resource resource = new ClassPathResource("templates/login.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

}

