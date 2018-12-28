package io.mosip.preregistration.transliteration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author M1043008
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class PreRegistrationTransliteration {
	public static void main(String[] args) {
		SpringApplication.run(PreRegistrationTransliteration.class, args);
	}
}
