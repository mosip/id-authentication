package io.mosip.registration.processor.quality.check;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:qc-user-application.properties")
public class QualityCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QualityCheckerApplication.class, args);
	}
}
