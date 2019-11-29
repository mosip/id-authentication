package io.mosip.resident;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.mosip.resident")

public class ResidentTestBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(ResidentTestBootApplication.class, args);
	}

}
