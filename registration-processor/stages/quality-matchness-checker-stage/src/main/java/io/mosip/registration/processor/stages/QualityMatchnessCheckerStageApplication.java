package io.mosip.registration.processor.stages;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.stages.quality.check.assignment.QualityCheckerAssignmentStage;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.stages",
		"io.mosip.registration.processor.quality.check", "io.mosip.registration.processor.auditmanager",
		"io.mosip.registration.processor.rest.client" })
public class QualityMatchnessCheckerStageApplication {

	@Autowired
	QualityCheckerAssignmentStage qualityCheckerAssignmentStage;

	public static void main(String[] args) {
		SpringApplication.run(QualityMatchnessCheckerStageApplication.class, args);
	}

	@PostConstruct
	public void deployVerticle() {
		qualityCheckerAssignmentStage.deployVerticle();
	}
}
