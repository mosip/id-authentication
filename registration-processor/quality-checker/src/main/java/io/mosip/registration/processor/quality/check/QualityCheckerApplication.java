package io.mosip.registration.processor.quality.check;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Class QualityCheckerApplication.
 */
@SpringBootApplication(scanBasePackages= {"io.mosip.registration.processor.auditmanager",
"io.mosip.registration.processor.quality.check"})
public class QualityCheckerApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(QualityCheckerApplication.class, args);
	}
}
