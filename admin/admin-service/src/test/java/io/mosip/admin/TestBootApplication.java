package io.mosip.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.mosip.admin.test.config.TestSecurityConfig;

@SpringBootApplication(scanBasePackages="io.mosip.admin")
@Import(TestSecurityConfig.class)
public class TestBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(TestBootApplication.class, args);
	}

}
