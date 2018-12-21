package io.mosip.preregistration.translitration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author M1043008
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class PreRegistrationTranslitration {
	public static void main(String[] args) {
		SpringApplication.run(PreRegistrationTranslitration.class, args);
	}
}
