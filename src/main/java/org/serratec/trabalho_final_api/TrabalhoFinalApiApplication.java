package org.serratec.trabalho_final_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableAsync
public class TrabalhoFinalApiApplication {

	public static void main(String[] args) {
		// Config do .env
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(TrabalhoFinalApiApplication.class, args);
	}
}