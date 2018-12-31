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
public class TransliterationApplication {
	public static void main(String[] args) {
		SpringApplication.run(TransliterationApplication.class, args);
	}
}
