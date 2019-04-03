package io.mosip.kernel.idgenerator.mispid.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * MispId Generator Application.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@SpringBootApplication
@ComponentScan({ "io.mosip.kernel.*" })
public class MispIdGeneratorBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(MispIdGeneratorBootApplication.class, args);

	}

}
