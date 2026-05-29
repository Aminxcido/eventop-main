package edu.upb.eventop;

import edu.upb.eventop.integracion.Sistema1AuthRequest;
import edu.upb.eventop.integracion.Sistema1AuthResponse;
import edu.upb.eventop.integracion.SistemaA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Slf4j
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@EnableJpaAuditing
@SpringBootApplication
public class EventopApplication implements CommandLineRunner {
	@Autowired
	private SistemaA sistemaA;

	public static void main(String[] args) {
		SpringApplication.run(EventopApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			Sistema1AuthRequest request = new Sistema1AuthRequest();
			request.setUsername("root");
			request.setPassword("Abc123**");
			Sistema1AuthResponse response = sistemaA.auth(request);
			log.info("Sistema1AuthResponse: {}", response);
		} catch (Exception e) {
			log.error("Error al conectar con SistemaA durante el inicio: {}", e.getMessage());
		}
	}

}
