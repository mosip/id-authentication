package io.mosip.registration.processor.quality.check;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages= {"io.mosip.registration.processor.auditmanager",
"io.mosip.registration.processor.quality.check"})
public class QualityCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QualityCheckerApplication.class, args);
	}
}
