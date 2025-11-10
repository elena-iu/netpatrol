HINWEIS: ES HANDELT SICH UM EINEN PROTOTYPEN IM RAHMEN EINER AKADEMISCHEN FALLSTUDIE

__________________________________

# NetPatrol

Eine Spring Boot Web-Anwendung zur Verwaltung und Koordination von Geisternetz-Meldungen und -Bergungen.

## 📋 Überblick

NetPatrol ist eine Plattform, die Taucher:innen, Fischer:innen und Küstenbewohner:innen dabei hilft, herrenlose Fischernetze (Geisternetze) zu dokumentieren, zu melden und deren Bergung zu koordinieren. Die Anwendung ermöglicht anonyme Meldungen, bietet eine interaktive Karte zur Visualisierung und unterstützt die Koordination zwischen Meldern und Bergungsteams.

## ✨ Features

- **Benutzerregistrierung und -authentifizierung**: Sichere Anmeldung mit Spring Security
- **Geisternetz-Meldungen**: 
  - Anonyme Meldungen möglich
  - Meldungen mit Benutzerkonto
  - GPS-basierte Standorterfassung
  - Verschollen-Meldungen mit Kontaktangaben
- **Dashboard**: Übersicht über alle Meldungen, Statistiken und zugewiesene Netze
- **Interaktive Karte**: Google Maps Integration zur Visualisierung von Geisternetzen
- **Statusverwaltung**: 
  - `GEMELDET`: Neues Geisternetz wurde gemeldet
  - `BERGUNG_BEVORSTEHEND`: Bergung wurde zugewiesen
  - `GEBORGEN`: Geisternetz wurde erfolgreich geborgen
  - `VERSCHOLLEN`: Geisternetz wurde als verschollen gemeldet
- **Zuweisungssystem**: Bergende Personen können Netze übernehmen
- **Umschlagungsanfragen**: System für die Übergabe von Bergungen zwischen Bergenden
- **Benachrichtigungssystem**: In-App Benachrichtigungen für wichtige Ereignisse

## 🛠️ Technologie-Stack

- **Framework**: Spring Boot 3.5.7
- **Java Version**: 25
- **Datenbank**: MySQL 8.0
- **Sicherheit**: Spring Security
- **Persistence**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Weitere Dependencies**:
  - Lombok
  - Spring Boot DevTools
  - Spring Boot Validation
  - Google Maps API (für Kartenvisualisierung)

## 📋 Voraussetzungen

- Java 25 oder höher
- Maven 3.6 oder höher
- MySQL 8.0 oder höher
- Google Maps API Key (optional, für Kartenfunktionalität)

## 🚀 Installation und Setup

### 1. Repository klonen

```bash
git clone <repository-url>
cd store
```

### 2. Datenbank einrichten

Erstelle eine MySQL-Datenbank:

```sql
CREATE DATABASE netpatrol;
```

### 3. Konfiguration anpassen

Bearbeite `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/netpatrol
spring.datasource.username=dein-username
spring.datasource.password=dein-passwort
server.port=6767
```

Für die Google Maps Integration, füge deinen API-Key in `src/main/resources/application-dev.properties` hinzu:

```properties
google.maps.api.key=dein-google-maps-api-key
```

### 4. Anwendung starten

```bash
mvn spring-boot:run
```

Die Anwendung ist dann unter `http://localhost:6767` erreichbar.

## 📁 Projektstruktur

```
store/
├── src/
│   ├── main/
│   │   ├── java/com/ghostnet/store/
│   │   │   ├── controller/          # REST Controller und Page Controller
│   │   │   ├── domain/              # JPA Entity Klassen
│   │   │   ├── repository/          # Spring Data JPA Repositories
│   │   │   ├── security/            # Spring Security Konfiguration
│   │   │   ├── service/             # Business Logic Services
│   │   │   └── NetPatrolApplication.java
│   │   └── resources/
│   │       ├── static/               # Statische Ressourcen (CSS, Bilder)
│   │       ├── templates/           # Thymeleaf Templates
│   │       └── application.properties
│   └── test/
│       └── java/                    # Test-Klassen
└── pom.xml
```

## 🔑 Wichtige Endpunkte

### Öffentliche Endpunkte
- `GET /` - Startseite
- `GET /login.html` - Login-Seite
- `GET /signup.html` - Registrierungsseite
- `POST /api/ghostnets` - Anonyme Geisternetz-Meldung

### Geschützte Endpunkte (Authentifizierung erforderlich)
- `GET /dashboard` - Dashboard-Übersicht
- `GET /meine-netze` - Eigene Meldungen und Zuweisungen
- `GET /meldungen` - Alle Meldungen mit Karte
- `GET /bergungen` - Bergungen und Umschlagungsanfragen
- `GET /api/user/**` - Benutzer-API
- `GET /api/ghostnets/**` - Geisternetz-API
- `POST /api/ghostnets/**` - Geisternetz erstellen/aktualisieren
- `POST /api/reassignment-requests/**` - Umschlagungsanfragen

## 🗄️ Datenmodell

### User
- Benutzerkonten mit Authentifizierung
- Rollen: Standard-Benutzer

### GhostNet
- Geisternetz-Meldungen mit GPS-Koordinaten
- Status-Tracking
- Zuweisung zu Bergenden Personen
- Reporter-Informationen

### ReassignmentRequest
- Anfragen für die Übergabe von Bergungen
- Status: PENDING, ACCEPTED, REJECTED

## 🔐 Sicherheit

- Spring Security für Authentifizierung und Autorisierung
- Passwörter werden verschlüsselt gespeichert (BCrypt)
- Session-basierte Authentifizierung
- CSRF-Schutz aktiviert
- Separate Berechtigungen für öffentliche und geschützte Routen

## 🧪 Tests

```bash
mvn test
```

## 📝 Entwicklung

### Hot Reload
Die Anwendung nutzt Spring Boot DevTools für automatisches Neuladen bei Code-Änderungen.

### Code-Style
- JavaDoc-Kommentare für alle öffentlichen Klassen und Methoden
- Konsistente Namensgebung nach Java-Konventionen


**NetPatrol** - Gemeinsam Meeresschutz stärken, Netz für Netz.

