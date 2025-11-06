package com.ghostnet.store.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity Klasse für Benutzer
 * Repräsentiert einen registrierten User im System
 */
@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    /** Benutzername für Login */
    private String username;
    
    /** Verschlüsseltes Passwort */
    private String password;
    
    /** Vollständiger Name des Benutzers */
    private String name;
    
    /** Telefonnummer des Benutzers */
    private String phone;
}
