package com.atsushini.hedgedocportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HedgedocPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(HedgedocPortalApplication.class, args);
	}

}
