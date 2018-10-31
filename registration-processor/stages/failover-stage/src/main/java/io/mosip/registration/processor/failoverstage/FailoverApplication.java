package io.mosip.registration.processor.failoverstage;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.retry.verticle",
		"io.mosip.registration.processor.failoverstage" })
class FailoverApplication {

	@Autowired
	FailoverStage failoverStage;

	public static void main(String[] args) {
		SpringApplication.run(FailoverApplication.class, args);
	}

	@PostConstruct
	public void deployeVerticle() {
		failoverStage.deployFailoverStage();
	}
}