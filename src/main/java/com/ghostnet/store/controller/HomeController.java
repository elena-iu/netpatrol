package com.ghostnet.store.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * Controller für die Startseite
 * Stellt die index.html Seite bereit
 */
@SuppressWarnings("null")
@Controller
public class HomeController {

    /**
     * Zeigt die Startseite an
     * @return index.html Template
     */
    @RequestMapping("/")
    public ResponseEntity<Resource> home() throws IOException {
        Resource resource = new ClassPathResource("templates/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    /**
     * Alternative Route zur Startseite
     * @return index.html Template
     */
    @GetMapping("/index.html")
    public ResponseEntity<Resource> index() throws IOException {
        Resource resource = new ClassPathResource("templates/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

}

