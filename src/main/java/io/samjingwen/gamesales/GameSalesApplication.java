package io.samjingwen.gamesales;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class GameSalesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameSalesApplication.class, args);
	}

}
