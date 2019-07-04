package io.mosip.kernel.idgenerator.partnerid.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Rid Generator Application.
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@SpringBootApplication
@ComponentScan({"io.mosip.kernel.*"})
public class PartnerIdGeneratorBootApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(PartnerIdGeneratorBootApplication.class, args);

	}

}
