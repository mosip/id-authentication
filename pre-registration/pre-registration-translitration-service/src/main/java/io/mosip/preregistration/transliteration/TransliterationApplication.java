/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This class is used to define the start of the transliteration application service
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*")
public class TransliterationApplication {
	/**
	 * @param args Unused
	 */
	public static void main(String[] args) {
		SpringApplication.run(TransliterationApplication.class, args);
	}
}
