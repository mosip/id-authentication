package io.mosip.registration.processor.retry.verticle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.retry.verticle.stages.RetryStage;

/**	
 * The Class RetryVerticleApplication.
 *
 * @author Jyoti prakash nayak
 */
@SpringBootApplication
public class RetryVerticleApplication {

	

	/** The retry satge. */
	@Autowired
	RetryStage retrySatge;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(RetryVerticleApplication.class, args);
	}

	/**
	 * method to deploy retry-stage.
	 */
	@PostConstruct
	void deployVerticle() {
		retrySatge.deployVerticle();
	}
}
