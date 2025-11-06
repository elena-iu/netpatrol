package com.ghostnet.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Hauptklasse der Spring Boot Anwendung für Geisternetz-Verwaltung
 * Aktiviert JPA Repositories für Datenbankzugriffe
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ghostnet.store.repository")
public class NetPatrolApplication {

	/**
	 * Einstiegspunkt der Anwendung
	 * @param args Kommandozeilenargumente
	 */
	public static void main(String[] args) {
		SpringApplication.run(NetPatrolApplication.class, args);
	}

}
