package io.mosip.kernel.keymanager.softhsm.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "io.mosip.kernel.keymanager.softhsm.*" })
public class KeymanagerBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(KeymanagerBootApplication.class, args);

	}

}
