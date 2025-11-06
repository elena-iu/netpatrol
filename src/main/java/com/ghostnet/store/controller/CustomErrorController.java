package com.ghostnet.store.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Controller für Fehlerbehandlung
 * Zeigt eine benutzerdefinierte Fehlerseite an
 */
@SuppressWarnings("null")
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Behandelt alle Fehleranfragen
     * @param request HTTP Request
     * @return error.html Template
     */
    @RequestMapping("/error")
    public ResponseEntity<Resource> handleError(HttpServletRequest request) throws IOException {
        // Immer die benutzerdefinierte Fehlerseite zurückgeben
        Resource resource = new ClassPathResource("templates/error.html");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
}

