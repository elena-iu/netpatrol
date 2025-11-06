package com.ghostnet.store.controller;

import com.ghostnet.store.domain.User;
import com.ghostnet.store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller für User-API
 * Stellt Endpoints für User-Informationen bereit
 */
@SuppressWarnings("null")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Liefert Informationen über den aktuell eingeloggten User
     * @return Map mit username, name und phone
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, String>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, String> response = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            String username = authentication.getName();
            response.put("username", username);
            
            // Vollständige User-Details abrufen
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                response.put("name", user.getName() != null ? user.getName() : "");
                response.put("phone", user.getPhone() != null ? user.getPhone() : "");
            }
        } else {
            response.put("username", "Benutzer");
            response.put("name", "");
            response.put("phone", "");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Findet einen User anhand der ID
     * @param id User-ID
     * @return User-Informationen
     */
    @GetMapping("/by-id/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("name", user.getName() != null ? user.getName() : "");
        response.put("phone", user.getPhone() != null ? user.getPhone() : "");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Findet einen User anhand des Benutzernamens
     * @param username Der Benutzername
     * @return User-Informationen
     */
    @GetMapping("/by-username/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("name", user.getName() != null ? user.getName() : "");
        response.put("phone", user.getPhone() != null ? user.getPhone() : "");
        return ResponseEntity.ok(response);
    }
}

