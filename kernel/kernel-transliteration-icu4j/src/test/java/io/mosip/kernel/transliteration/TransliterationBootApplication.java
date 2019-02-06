package io.mosip.kernel.transliteration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.kernel.*")
public class TransliterationBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(TransliterationBootApplication.class, args);
	}
}
