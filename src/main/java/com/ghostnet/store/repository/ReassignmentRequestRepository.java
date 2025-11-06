package com.ghostnet.store.repository;

import com.ghostnet.store.domain.ReassignmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Interface für ReassignmentRequest-Entity
 * Stellt Datenbankzugriffe für Umverteilungsanfragen bereit
 */
@Repository
public interface ReassignmentRequestRepository extends JpaRepository<ReassignmentRequest, Long> {
    /** Findet Anfragen für einen Berger mit bestimmtem Status, sortiert nach Erstellungsdatum */
    List<ReassignmentRequest> findByCurrentSalvorIdAndStatusOrderByCreatedAtDesc(Long salvorId, String status);
    
    /** Findet Anfragen eines Antragstellers mit bestimmtem Status, sortiert nach Erstellungsdatum */
    List<ReassignmentRequest> findByRequesterIdAndStatusOrderByCreatedAtDesc(Long requesterId, String status);
    
    /** Findet alle Anfragen eines Antragstellers, sortiert nach Erstellungsdatum */
    List<ReassignmentRequest> findByRequesterIdOrderByCreatedAtDesc(Long requesterId);
    
    /** Findet Anfragen für ein Geisternetz mit bestimmtem Status */
    List<ReassignmentRequest> findByGhostNetIdAndStatus(Long ghostNetId, String status);
}

