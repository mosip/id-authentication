package io.mosip.registration.processor.bio.dedupe.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.bio.dedupe" })
public class BioDedupeApiApp {
	public static void main(String[] args) {
		SpringApplication.run(BioDedupeApiApp.class, args);
	}
}
