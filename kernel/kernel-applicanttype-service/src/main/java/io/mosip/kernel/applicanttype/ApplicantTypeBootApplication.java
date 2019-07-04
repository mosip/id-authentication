package io.mosip.kernel.applicanttype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @author Bal Vikash Sharma
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.applicanttype.*", "io.mosip.kernel.auth.*" })
public class ApplicantTypeBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplicantTypeBootApplication.class, args);
	}

}
//http://localhost:8080/swagger-ui.html
