package io.mosip.kernel.vidgenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.kernel.core.spi.idgenerator.MosipVidGenerator;

/*
 * (non-Javadoc)
 * 
 * VidGenerator Client (Demo)
 */

@SpringBootApplication
public class VidGeneratorApplication implements CommandLineRunner {

	@Autowired
	MosipVidGenerator<String> vidGenerator;

	public static void main(String[] args) {
		SpringApplication.run(VidGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n\n Generated VID : " + vidGenerator.generateId("890789978978"));
		System.out.println(" Generated VID : " + vidGenerator.generateId("890789978978") + "\n\n");

	}
}
