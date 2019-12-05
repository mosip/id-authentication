package io.mosip.resident;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"io.mosip.resident.*", "io.mosip.kernel.*"})
public class ResidentTestBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResidentTestBootApplication.class, args);
	}

}
