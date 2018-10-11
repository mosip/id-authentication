package io.mosip.kernel.pridgenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.kernel.core.spi.idgenerator.MosipPridGenerator;

/*
 * (non-Javadoc)
 * 
 * PridGenerator Client (Demo)
 */

@SpringBootApplication
public class PridGeneratorApplication implements CommandLineRunner {

	@Autowired
	MosipPridGenerator<String> pridGenerator;

	public static void main(String[] args) {
		SpringApplication.run(PridGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n\n Generated PRID : " + pridGenerator.generateId() + "\n\n");
	}
}
