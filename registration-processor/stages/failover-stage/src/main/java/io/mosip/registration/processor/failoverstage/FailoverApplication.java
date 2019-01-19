package io.mosip.registration.processor.failoverstage;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Class FailoverApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.retry.verticle",
		"io.mosip.registration.processor.failoverstage" })
class FailoverApplication {
	
	/** The failover stage. */
	@Autowired
	FailoverStage failoverStage;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(FailoverApplication.class, args);
	}

	/**
	 * Deploye verticle.
	 */
	@PostConstruct
	public void deployeVerticle() {
		failoverStage.deployFailoverStage();
	}
}