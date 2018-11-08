package io.mosip.registration.processor.stages;
import io.mosip.registration.processor.stages.quality.check.assignment.QualityCheckerAssignmentStage;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.stages",
		"io.mosip.registration.processor.quality.check" })
@PropertySource({ "classpath:qc-user-application.properties" })
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
