package io.mosip.registration.processor.stages;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.stages.quality.check.assignment.QualityCheckerAssignmentStage;


/**
 * The Class QualityMatchnessCheckerStageApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.stages",
		"io.mosip.registration.processor.quality.check", "io.mosip.registration.processor.auditmanager",
		"io.mosip.registration.processor.rest.client" })
public class QualityMatchnessCheckerStageApplication {

	/** The quality checker assignment stage. */
	@Autowired
	QualityCheckerAssignmentStage qualityCheckerAssignmentStage;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(QualityMatchnessCheckerStageApplication.class, args);
	}

	/**
	 * Deploy verticle.
	 */
	@PostConstruct
	public void deployVerticle() {
		qualityCheckerAssignmentStage.deployVerticle();
	}
}
