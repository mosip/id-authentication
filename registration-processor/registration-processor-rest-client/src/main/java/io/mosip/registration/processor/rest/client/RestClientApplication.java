package io.mosip.registration.processor.rest.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.rest.client" })
public class RestClientApplication {

	@Autowired
	public static void main(String[] args) {
		SpringApplication.run(RestClientApplication.class, args);
	}

}
