package com.ghostnet.store.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity Klasse für Geisternetze
 * Repräsentiert ein gemeldetes oder zu bergendes Geisternetz
 */
@Getter
@Setter
@Entity
@Table(name = "ghost_net")
public class GhostNet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    /** Textuelle Beschreibung des Standorts */
    private String location;
    
    /** Breitengrad der Position */
    private Double latitude;
    
    /** Längengrad der Position */
    private Double longitude;
    
    /** Status: GEMELDET, BERGUNG_BEVORSTEHEND, GEBORGEN, VERSCHOLLEN */
    private String status;
    
    @Column(name = "estimated_size")
    /** Geschätzte Größe des Geisternetzes */
    private String estimatedSize;
    
    /** Zusätzliche Beschreibung */
    private String description;
    
    @Column(name = "created_at")
    /** Zeitpunkt der Erstellung des Datensatzes */
    private LocalDateTime createdAt;
    
    @Column(name = "reporter_name")
    /** Name der Person, die das Geisternetz gemeldet hat */
    private String reporterName;
    
    /** Zeitpunkt der Meldung */
    private LocalDateTime reportedAt;
    
    /** Username der Person, die das Geisternetz gemeldet hat */
    private String reportedBy;
    
    @Column(name = "salvor_user_id")
    /** ID des Users, dem die Bergung zugewiesen ist */
    private Long salvorUserId;
    
    @Column(name = "reporter_id")
    /** ID des Users, der das Geisternetz gemeldet hat */
    private Long reporterId;
    
    @Column(name = "missing_reporter_name")
    /** Name der Person, die das Verschwinden gemeldet hat (für VERSCHOLLEN Status) */
    private String missingReporterName;
    
    @Column(name = "missing_reporter_phone")
    /** Telefonnummer der Person, die das Verschwinden gemeldet hat (für VERSCHOLLEN Status) */
    private String missingReporterPhone;
    
}

