package io.mosip.kernel.idgenerator.tsp.test.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Rid Generator Application.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@SpringBootApplication
@ComponentScan({"io.mosip.kernel.*"})
public class TspIdGeneratorBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(TspIdGeneratorBootApplication.class, args);

	}

}
