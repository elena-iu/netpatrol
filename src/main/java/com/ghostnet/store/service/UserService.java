package com.ghostnet.store.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.ghostnet.store.repository.UserRepository;
import com.ghostnet.store.domain.User;
import java.util.Optional;
import lombok.AllArgsConstructor;

/**
 * Service Klasse für User-Management
 * Implementiert Spring Security UserDetailsService für Authentication
 */
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    /**
     * Lädt User-Daten für Spring Security Authentication
     * @param username Der Benutzername
     * @return UserDetails für Spring Security
     * @throws UsernameNotFoundException wenn User nicht gefunden wird
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findByUsername(username);
        if (user.isPresent()) {
            var userObj = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                .username(userObj.getUsername())
                .password(userObj.getPassword())
                .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

}
