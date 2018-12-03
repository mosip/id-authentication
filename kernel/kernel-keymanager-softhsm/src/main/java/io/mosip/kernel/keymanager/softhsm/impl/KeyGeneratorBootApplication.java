package io.mosip.kernel.keymanager.softhsm.impl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"io.mosip.kernel.*"})
public class KeyGeneratorBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(KeyGeneratorBootApplication.class, args);

	}

}
