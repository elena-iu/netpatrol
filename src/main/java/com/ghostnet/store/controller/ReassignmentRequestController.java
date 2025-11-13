package com.ghostnet.store.controller;

import com.ghostnet.store.domain.GhostNet;
import com.ghostnet.store.domain.ReassignmentRequest;
import com.ghostnet.store.domain.User;
import com.ghostnet.store.repository.GhostNetRepository;
import com.ghostnet.store.repository.ReassignmentRequestRepository;
import com.ghostnet.store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller für Umverteilungsanfragen-API
 * Verwaltet Anfragen zur Übertragung der Bergungsverantwortung für Geisternetze
 */
@SuppressWarnings("null")
@RestController
@RequestMapping("/api/reassignment-requests")
public class ReassignmentRequestController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReassignmentRequestController.class);

    @Autowired
    private ReassignmentRequestRepository requestRepository;
    
    @Autowired
    private GhostNetRepository ghostNetRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Erstellt eine neue Umverteilungsanfrage
     * Ein User kann anfragen, die Bergungsverantwortung für ein Geisternetz zu übernehmen
     * @param payload Map mit ghostNetId
     * @return Erstellte Anfrage
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRequest(@RequestBody Map<String, Long> payload) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return ResponseEntity.status(401).build();
            }
            
            Long ghostNetId = payload.get("ghostNetId");
            if (ghostNetId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            var ghostNetOpt = ghostNetRepository.findById(ghostNetId);
            if (ghostNetOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            GhostNet ghostNet = ghostNetOpt.get();
            if (ghostNet.getSalvorUserId() == null) {
                return ResponseEntity.badRequest().build(); // Kein Bergender zugewiesen
            }
            
            String username = authentication.getName();
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).build();
            }
            
            User requester = userOpt.get();
            
            // Prüfen ob Antragsteller bereits der aktuelle Berger ist
            if (ghostNet.getSalvorUserId().equals(requester.getId())) {
                return ResponseEntity.badRequest().build(); // Kann keine Umverteilung für eigene Zuweisung anfragen
            }
            
            // Prüfen ob bereits eine offene Anfrage für dieses Geisternetz existiert
            List<ReassignmentRequest> existing = requestRepository.findByGhostNetIdAndStatus(
                ghostNetId, "PENDING");
            if (!existing.isEmpty()) {
                return ResponseEntity.badRequest().build(); // Anfrage existiert bereits
            }
            
            // Neue Anfrage erstellen
            ReassignmentRequest request = new ReassignmentRequest();
            request.setGhostNetId(ghostNetId);
            request.setRequesterId(requester.getId());
            request.setCurrentSalvorId(ghostNet.getSalvorUserId());
            request.setStatus("PENDING");
            request.setCreatedAt(LocalDateTime.now());
            
            ReassignmentRequest saved = requestRepository.save(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("ghostNetId", saved.getGhostNetId());
            response.put("requesterId", saved.getRequesterId());
            response.put("status", saved.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating reassignment request", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Liefert alle Anfragen, die der aktuelle User gestellt hat
     * @return Liste der gesendeten Anfragen
     */
    @GetMapping("/my-sent")
    public ResponseEntity<List<Map<String, Object>>> getMySentRequests() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return ResponseEntity.status(401).build();
            }
            
            String username = authentication.getName();
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).build();
            }
            
            User user = userOpt.get();
            List<ReassignmentRequest> requests = requestRepository.findByRequesterIdOrderByCreatedAtDesc(user.getId());
            
            List<Map<String, Object>> response = requests.stream().map(req -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", req.getId());
                map.put("ghostNetId", req.getGhostNetId());
                map.put("status", req.getStatus());
                map.put("createdAt", req.getCreatedAt());
                
                var ghostNetOpt = ghostNetRepository.findById(req.getGhostNetId());
                if (ghostNetOpt.isPresent()) {
                    GhostNet net = ghostNetOpt.get();
                    map.put("ghostNetLocation", net.getLatitude() != null && net.getLongitude() != null ?
                        net.getLatitude() + ", " + net.getLongitude() : net.getLocation());
                    map.put("ghostNetSize", net.getEstimatedSize());
                }
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching my sent requests", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Liefert alle Anfragen, die für den aktuellen User als Berger gestellt wurden
     * Diese Anfragen können angenommen oder abgelehnt werden
     * @return Liste der eingehenden Anfragen mit Status PENDING
     */
    @GetMapping("/my-requests")
    public ResponseEntity<List<Map<String, Object>>> getMyRequests() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return ResponseEntity.status(401).build();
            }
            
            String username = authentication.getName();
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).build();
            }
            
            User user = userOpt.get();
            List<ReassignmentRequest> requests = requestRepository.findByCurrentSalvorIdAndStatusOrderByCreatedAtDesc(
                user.getId(), "PENDING");
            
            List<Map<String, Object>> response = requests.stream().map(req -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", req.getId());
                map.put("ghostNetId", req.getGhostNetId());
                map.put("requesterId", req.getRequesterId());
                map.put("createdAt", req.getCreatedAt());
                
                // Antragsteller-Informationen abrufen
                var requesterOpt = userRepository.findById(req.getRequesterId());
                if (requesterOpt.isPresent()) {
                    User requester = requesterOpt.get();
                    map.put("requesterUsername", requester.getUsername());
                    map.put("requesterName", requester.getName() != null ? requester.getName() : "");
                }
                
                // Geisternetz-Informationen abrufen
                var ghostNetOpt = ghostNetRepository.findById(req.getGhostNetId());
                if (ghostNetOpt.isPresent()) {
                    GhostNet net = ghostNetOpt.get();
                    map.put("ghostNetLocation", net.getLatitude() != null && net.getLongitude() != null ?
                        net.getLatitude() + ", " + net.getLongitude() : net.getLocation());
                    map.put("ghostNetSize", net.getEstimatedSize());
                }
                
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching requests", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Nimmt eine Umverteilungsanfrage an
     * Überträgt die Bergungsverantwortung auf den Antragsteller
     * Alle anderen offenen Anfragen für dasselbe Geisternetz werden abgelehnt
     * @param id ID der Anfrage
     * @return Erfolgsmeldung
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<Map<String, Object>> acceptRequest(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return ResponseEntity.status(401).build();
            }
            
            var requestOpt = requestRepository.findById(id);
            if (requestOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            ReassignmentRequest request = requestOpt.get();
            
            // Prüfen ob der aktuelle User der aktuelle Bergende ist
            String username = authentication.getName();
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).build();
            }
            
            User user = userOpt.get();
            if (request.getCurrentSalvorId() == null || !request.getCurrentSalvorId().equals(user.getId())) {
                return ResponseEntity.status(403).build(); // Nicht autorisiert - nicht der aktuelle Berger
            }
            
            if (!"PENDING".equals(request.getStatus())) {
                return ResponseEntity.badRequest().build(); // Bereits bearbeitet
            }
            
            // Geisternetz aktualisieren
            var ghostNetOpt = ghostNetRepository.findById(request.getGhostNetId());
            if (ghostNetOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            GhostNet ghostNet = ghostNetOpt.get();
            ghostNet.setSalvorUserId(request.getRequesterId());
            ghostNetRepository.save(ghostNet);
            
            // Anfrage-Status aktualisieren
            request.setStatus("ACCEPTED");
            requestRepository.save(request);
            
            // Alle anderen offenen Anfragen für dieses Geisternetz ablehnen
            List<ReassignmentRequest> otherRequests = requestRepository.findByGhostNetIdAndStatus(
                request.getGhostNetId(), "PENDING");
            for (ReassignmentRequest other : otherRequests) {
                if (!other.getId().equals(id)) {
                    other.setStatus("REJECTED");
                    requestRepository.save(other);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error accepting request", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Lehnt eine Umverteilungsanfrage ab
     * Nur der aktuelle Berger kann eine Anfrage ablehnen
     * @param id ID der Anfrage
     * @return Erfolgsmeldung
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectRequest(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return ResponseEntity.status(401).build();
            }
            
            var requestOpt = requestRepository.findById(id);
            if (requestOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            ReassignmentRequest request = requestOpt.get();
            
            // Prüfen ob der aktuelle User der aktuelle Bergende ist
            String username = authentication.getName();
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).build();
            }
            
            User user = userOpt.get();
            if (request.getCurrentSalvorId() == null || !request.getCurrentSalvorId().equals(user.getId())) {
                return ResponseEntity.status(403).build(); // Nicht autorisiert - nicht der aktuelle Berger
            }
            
            if (!"PENDING".equals(request.getStatus())) {
                return ResponseEntity.badRequest().build(); // Bereits bearbeitet
            }
            
            // Anfrage-Status auf REJECTED setzen
            request.setStatus("REJECTED");
            requestRepository.save(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error rejecting request", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

