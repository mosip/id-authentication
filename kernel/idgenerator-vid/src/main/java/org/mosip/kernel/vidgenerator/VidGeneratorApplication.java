package org.mosip.kernel.vidgenerator;

import org.mosip.kernel.vidgenerator.generator.VidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * (non-Javadoc)
 * 
 * VidGenerator Client (Demo)
 */

@SpringBootApplication
public class VidGeneratorApplication implements CommandLineRunner {

	@Autowired
	VidGenerator vidGenerator;

	public static void main(String[] args) {
		SpringApplication.run(VidGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n\n Generated VID : " + vidGenerator.generateId("890789978978") + "\n\n");
	}
}
