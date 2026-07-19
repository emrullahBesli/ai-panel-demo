package dev.emrullah.ai_panel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AiPanelApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiPanelApplication.class, args);
	}

}
