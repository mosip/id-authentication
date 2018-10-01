package org.mosip.kernel.pridgenerator;

import org.mosip.kernel.pridgenerator.generator.PridGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * (non-Javadoc)
 * 
 * PridGenerator Client (Demo)
 */

@SpringBootApplication
public class PridGeneratorApplication implements CommandLineRunner {

	@Autowired
	PridGenerator pridGenerator;

	public static void main(String[] args) {
		SpringApplication.run(PridGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n\n Generated PRID : " + pridGenerator.generateId() + "\n\n");
	}
}
