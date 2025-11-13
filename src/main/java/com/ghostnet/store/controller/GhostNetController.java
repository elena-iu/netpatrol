package com.ghostnet.store.controller;

import com.ghostnet.store.domain.GhostNet;
import com.ghostnet.store.domain.User;
import com.ghostnet.store.repository.GhostNetRepository;
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

/**
 * Controller für Geisternetz-API
 * Stellt alle Endpoints für CRUD-Operationen auf Geisternetzen bereit
 * Verwaltet Meldungen, Zuweisungen, Bergungen und Statusänderungen
 */
@SuppressWarnings("null")
@RestController
@RequestMapping("/api/ghostnets")
public class GhostNetController {
    
    private static final Logger logger = LoggerFactory.getLogger(GhostNetController.class);

    @Autowired
    private GhostNetRepository ghostNetRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Liefert alle Geisternetze
     * Sortiert nach Erstellungsdatum, mit Fallback auf Meldungsdatum
     * @return Liste aller Geisternetze
     */
    @GetMapping
    public ResponseEntity<List<GhostNet>> getAllGhostNets() {
        try {
            List<GhostNet> ghostNets;
            try {
                ghostNets = ghostNetRepository.findAllByOrderByCreatedAtDesc();
            } catch (Exception e) {
                // Fallback auf Meldungsdatum-Sortierung falls Erstellungsdatum fehlschlägt
                try {
                    ghostNets = ghostNetRepository.findAllByOrderByReportedAtDesc();
                } catch (Exception e2) {
                    // Finaler Fallback auf findAll falls beide Sortierungen fehlschlagen
                    logger.warn("Ordering failed, using findAll: {}", e2.getMessage());
                    ghostNets = ghostNetRepository.findAll();
                }
            }
            return ResponseEntity.ok(ghostNets);
        } catch (Exception e) {
            logger.error("Error fetching ghost nets", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Erstellt ein neues Geisternetz
     * Setzt Status auf GEMELDET und speichert Melder-Informationen
     * @param ghostNet Geisternetz-Daten
     * @return Gespeichertes Geisternetz
     */
    @PostMapping
    public ResponseEntity<GhostNet> createGhostNet(@RequestBody GhostNet ghostNet) {
        try {
            // Status auf GEMELDET setzen
            ghostNet.setStatus("GEMELDET");
            
            // Zeitstempel setzen
            LocalDateTime now = LocalDateTime.now();
            ghostNet.setCreatedAt(now);
            ghostNet.setReportedAt(now);
            
            // Aktuellen User abrufen und Melder-Informationen setzen
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
                String username = authentication.getName();
                var userOpt = userRepository.findByUsername(username);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    ghostNet.setReporterName(user.getName() != null ? user.getName() : "");
                    ghostNet.setReportedBy(username);
                    ghostNet.setReporterId(user.getId());
                }
            }
            
            GhostNet saved = ghostNetRepository.save(ghostNet);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("Error creating ghost net", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Liefert alle Geisternetze, die vom aktuellen User gemeldet wurden
     * @return Liste der gemeldeten Geisternetze
     */
    @GetMapping("/my-reports")
    public ResponseEntity<List<GhostNet>> getMyReports() {
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
            List<GhostNet> ghostNets = ghostNetRepository.findByReporterIdOrderByCreatedAtDesc(user.getId());
            return ResponseEntity.ok(ghostNets);
        } catch (Exception e) {
            logger.error("Error fetching my reports", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Liefert alle Geisternetze, die dem aktuellen User zur Bergung zugewiesen sind
     * @return Liste der zugewiesenen Geisternetze
     */
    @GetMapping("/my-assignments")
    public ResponseEntity<List<GhostNet>> getMyAssignments() {
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
            List<GhostNet> ghostNets = ghostNetRepository.findBySalvorUserIdOrderByCreatedAtDesc(user.getId());
            return ResponseEntity.ok(ghostNets);
        } catch (Exception e) {
            logger.error("Error fetching my assignments", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Weist ein Geisternetz dem aktuellen User zur Bergung zu
     * Ändert Status auf BERGUNG_BEVORSTEHEND
     * @param id ID des Geisternetzes
     * @return Aktualisiertes Geisternetz
     */
    @PutMapping("/{id}/assign")
    public ResponseEntity<GhostNet> assignGhostNet(@PathVariable Long id) {
        try {
            // Geisternetz abrufen
            var ghostNetOpt = ghostNetRepository.findById(id);
            if (ghostNetOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            GhostNet ghostNet = ghostNetOpt.get();
            
            // Prüfen ob Geisternetz im Status GEMELDET ist
            String currentStatus = ghostNet.getStatus();
            if (currentStatus == null || !"GEMELDET".equalsIgnoreCase(currentStatus)) {
                return ResponseEntity.badRequest().build();
            }
            
            // Aktuellen User abrufen
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
            Long userId = user.getId();
            
            // Aktuellen User als Berger zuweisen
            ghostNet.setSalvorUserId(userId);
            ghostNet.setStatus("BERGUNG_BEVORSTEHEND");
            
            GhostNet saved = ghostNetRepository.save(ghostNet);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("Error assigning ghost net", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Löscht ein Geisternetz
     * Nur der Melder kann löschen, und nur bei Status GEMELDET oder VERSCHOLLEN
     * @param id ID des Geisternetzes
     * @return Erfolgreiche Antwort ohne Body
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGhostNet(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return ResponseEntity.status(401).build();
            }
            
            var ghostNetOpt = ghostNetRepository.findById(id);
            if (ghostNetOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            GhostNet ghostNet = ghostNetOpt.get();
            
            // Nur erlauben wenn User der Melder ist und Status GEMELDET oder VERSCHOLLEN
            String username = authentication.getName();
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).build();
            }
            
            User user = userOpt.get();
            if (ghostNet.getReporterId() == null || !ghostNet.getReporterId().equals(user.getId())) {
                return ResponseEntity.status(403).build(); // Nicht autorisiert - nicht der Melder
            }
            
            String currentStatus = ghostNet.getStatus();
            if (currentStatus == null || (!"GEMELDET".equalsIgnoreCase(currentStatus) && !"VERSCHOLLEN".equalsIgnoreCase(currentStatus))) {
                return ResponseEntity.badRequest().build(); // Nur GEMELDET oder VERSCHOLLEN können gelöscht werden
            }
            
            ghostNetRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting ghost net", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Markiert ein Geisternetz als geborgen
     * Ändert Status auf GEBORGEN
     * Nur der zugewiesene Berger kann bergen
     * @param id ID des Geisternetzes
     * @return Aktualisiertes Geisternetz
     */
    @PutMapping("/{id}/recover")
    public ResponseEntity<GhostNet> recoverGhostNet(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return ResponseEntity.status(401).build();
            }
            
            var ghostNetOpt = ghostNetRepository.findById(id);
            if (ghostNetOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            GhostNet ghostNet = ghostNetOpt.get();
            
            // Nur erlauben wenn User der zugewiesene Berger ist
            String username = authentication.getName();
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).build();
            }
            
            User user = userOpt.get();
            if (ghostNet.getSalvorUserId() == null || !ghostNet.getSalvorUserId().equals(user.getId())) {
                return ResponseEntity.status(403).build(); // Nicht autorisiert - nicht der zugewiesene Berger
            }
            
            // Status auf GEBORGEN setzen
            ghostNet.setStatus("GEBORGEN");
            
            GhostNet saved = ghostNetRepository.save(ghostNet);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("Error recovering ghost net", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Markiert ein Geisternetz als verschollen
     * Ändert Status auf VERSCHOLLEN
     * Setzt Kontaktinformationen gemäß Datenbank-Constraint
     * @param id ID des Geisternetzes
     * @return Aktualisiertes Geisternetz oder Fehlermeldung
     */
    @PutMapping("/{id}/mark-missing")
    public ResponseEntity<?> markAsMissing(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getName().equals("anonymousUser")) {
                return ResponseEntity.status(401).build();
            }
            
            var ghostNetOpt = ghostNetRepository.findById(id);
            if (ghostNetOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            GhostNet ghostNet = ghostNetOpt.get();
            
            // Prüfen ob bereits GEBORGEN - kann nicht als verschollen markiert werden wenn bereits geborgen
            String currentStatus = ghostNet.getStatus();
            if (currentStatus != null && "GEBORGEN".equalsIgnoreCase(currentStatus)) {
                return ResponseEntity.badRequest().build(); // Geborgene Netze können nicht als verschollen markiert werden
            }
            
            // Aktuellen User für Kontaktinformationen abrufen
            String username = authentication.getName();
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).build(); // User not found
            }
            User user = userOpt.get();
            
            // Kontaktinformationen sicherstellen (erforderlich durch Datenbank-Constraint chk_missing_contact)
            // Der Constraint verlangt nicht-leere Kontaktinformationen wenn Status VERSCHOLLEN ist
            // Wir müssen reporter_name, reported_by und reporter_id auf nicht-null, nicht-leere Werte setzen
            
            // reporter_name setzen - User-Name oder Username als Fallback verwenden
            String reporterName = ghostNet.getReporterName();
            if (reporterName == null || reporterName.trim().isEmpty()) {
                reporterName = (user.getName() != null && !user.getName().trim().isEmpty()) 
                    ? user.getName() 
                    : username;
                ghostNet.setReporterName(reporterName);
            }
            
            // reported_by setzen - immer username verwenden (für authentifizierte User garantiert nicht-leer)
            String reportedBy = ghostNet.getReportedBy();
            if (reportedBy == null || reportedBy.trim().isEmpty()) {
                reportedBy = username;
                ghostNet.setReportedBy(reportedBy);
            }
            
            // reporter_id setzen - muss nicht-null sein
            Long reporterId = ghostNet.getReporterId();
            if (reporterId == null) {
                reporterId = user.getId();
                if (reporterId == null) {
                    return ResponseEntity.status(500).body(Map.of("error", "User ID is null"));
                }
                ghostNet.setReporterId(reporterId);
            }
            
            // Finale Validierung - sicherstellen dass alle Felder nicht-null und nicht-leer sind
            if (ghostNet.getReporterName() == null || ghostNet.getReporterName().trim().isEmpty() ||
                ghostNet.getReportedBy() == null || ghostNet.getReportedBy().trim().isEmpty() ||
                ghostNet.getReporterId() == null) {
                return ResponseEntity.status(500).body(Map.of("error", 
                    "Contact information validation failed: reporterName='" + ghostNet.getReporterName() + 
                    "', reportedBy='" + ghostNet.getReportedBy() + 
                    "', reporterId=" + ghostNet.getReporterId()));
            }
            
            // Log-Eintrag für Debugging
            logger.debug("Setting contact info - reporterName: '{}', reportedBy: '{}', reporterId: {}", 
                        ghostNet.getReporterName(), ghostNet.getReportedBy(), ghostNet.getReporterId());
            
            // missing_reporter_name und missing_reporter_phone setzen (erforderlich durch chk_missing_contact Constraint)
            // Diese Felder sind erforderlich wenn Status VERSCHOLLEN ist und dürfen NICHT NULL sein
            String missingReporterName = (user.getName() != null && !user.getName().trim().isEmpty()) 
                ? user.getName() 
                : username;
            ghostNet.setMissingReporterName(missingReporterName);
            
            // Telefonnummer darf nicht NULL sein - User-Telefon oder Standardwert verwenden
            String missingReporterPhone = (user.getPhone() != null && !user.getPhone().trim().isEmpty()) 
                ? user.getPhone() 
                : "N/A"; // Standardwert wenn Telefonnummer nicht verfügbar ist (Constraint verlangt nicht-NULL)
            ghostNet.setMissingReporterPhone(missingReporterPhone);
            
            // Status auf VERSCHOLLEN setzen
            ghostNet.setStatus("VERSCHOLLEN");
            
            try {
                GhostNet saved = ghostNetRepository.save(ghostNet);
                return ResponseEntity.ok(saved);
            } catch (Exception saveException) {
                logger.error("Error saving ghost net", saveException);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Failed to save ghost net: " + saveException.getMessage());
                return ResponseEntity.status(500).body(error);
            }
        } catch (Exception e) {
            logger.error("Error marking ghost net as missing", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error marking ghost net as missing: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Liefert Statistiken für den aktuellen User
     * Anzahl gemeldeter und geborgener Geisternetze
     * @return Map mit "reported" und "recovered" Counts
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getMyStats() {
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
            Long userId = user.getId();
            
            // Gemeldete Netze zählen (alle Netze wo reporterId übereinstimmt)
            long reportedCount = ghostNetRepository.countByReporterId(userId);
            
            // Geborgene Netze zählen (User zugewiesene Netze mit Status GEBORGEN)
            long recoveredCount = ghostNetRepository.countBySalvorUserIdAndStatus(userId, "GEBORGEN");
            
            Map<String, Long> stats = new HashMap<>();
            stats.put("reported", reportedCount);
            stats.put("recovered", recoveredCount);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching stats", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

