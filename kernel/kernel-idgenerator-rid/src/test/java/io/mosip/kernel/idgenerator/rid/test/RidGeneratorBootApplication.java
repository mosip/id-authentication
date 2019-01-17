package io.mosip.kernel.idgenerator.rid.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Rid Generator Application.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@SpringBootApplication
@ComponentScan({"io.mosip.kernel.*"})
public class RidGeneratorBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(RidGeneratorBootApplication.class, args);

	}

}
