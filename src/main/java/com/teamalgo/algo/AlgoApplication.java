package com.teamalgo.algo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AlgoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgoApplication.class, args);
	}

}
