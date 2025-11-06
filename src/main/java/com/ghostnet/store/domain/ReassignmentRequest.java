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
 * Entity Klasse für Umverteilungsanfragen
 * Repräsentiert eine Anfrage zur Übertragung der Bergungsverantwortung
 */
@Getter
@Setter
@Entity
@Table(name = "reassignment_request")
public class ReassignmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "ghost_net_id")
    /** ID des Geisternetzes, für das die Umverteilung angefragt wird */
    private Long ghostNetId;
    
    @Column(name = "requester_id")
    /** ID des Users, der die Umverteilung anfragt */
    private Long requesterId;
    
    @Column(name = "current_salvor_id")
    /** ID des aktuell zugewiesenen Bergers */
    private Long currentSalvorId;
    
    @Column(name = "status")
    /** Status: PENDING, ACCEPTED, REJECTED */
    private String status;
    
    @Column(name = "created_at")
    /** Zeitpunkt der Anfrage */
    private LocalDateTime createdAt;
    
}

