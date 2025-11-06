package com.ghostnet.store.repository;

import com.ghostnet.store.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository Interface für User-Entity
 * Stellt Datenbankzugriffe für Benutzer bereit
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Findet einen User anhand des Benutzernamens
     * @param username Der Benutzername
     * @return Optional mit dem User, falls gefunden
     */
    Optional<User> findByUsername(String username);
}
