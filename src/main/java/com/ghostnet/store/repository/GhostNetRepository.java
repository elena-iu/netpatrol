package com.ghostnet.store.repository;

import com.ghostnet.store.domain.GhostNet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Interface für GhostNet-Entity
 * Stellt Datenbankzugriffe für Geisternetze bereit
 */
@Repository
public interface GhostNetRepository extends JpaRepository<GhostNet, Long> {
    /** Findet alle Geisternetze sortiert nach Erstellungsdatum (neueste zuerst) */
    List<GhostNet> findAllByOrderByCreatedAtDesc();
    
    /** Findet alle Geisternetze sortiert nach Meldungsdatum (neueste zuerst) */
    List<GhostNet> findAllByOrderByReportedAtDesc();
    
    /** Findet alle Geisternetze eines Melders sortiert nach Erstellungsdatum */
    List<GhostNet> findByReporterIdOrderByCreatedAtDesc(Long reporterId);
    
    /** Findet alle Geisternetze eines Bergers sortiert nach Erstellungsdatum */
    List<GhostNet> findBySalvorUserIdOrderByCreatedAtDesc(Long salvorUserId);
    
    /** Zählt alle Geisternetze eines Melders */
    long countByReporterId(Long reporterId);
    
    /** Zählt alle Geisternetze eines Bergers mit bestimmten Status */
    long countBySalvorUserIdAndStatus(Long salvorUserId, String status);
}

