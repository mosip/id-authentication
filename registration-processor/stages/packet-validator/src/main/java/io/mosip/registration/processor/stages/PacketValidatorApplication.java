package io.mosip.registration.processor.stages;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import io.mosip.registration.processor.stages.packet.validator.PacketValidatorStage;

/**
 * The Class PacketValidatorApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.stages",
		"io.mosip.registration.processor.status", "io.mosip.registration.processor.packet.manager", "io.mosip.registration.processor.packet.storage" })
@PropertySource({ "classpath:validator-application.properties" })
@PropertySource({ "classpath:status-application.properties" })
@PropertySource({ "classpath:packet-manager-application.properties" })
public class PacketValidatorApplication {

	/** The validatebean. */
	@Autowired
	private PacketValidatorStage validatebean;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketValidatorApplication.class, args);
	}

	/**
	 * Deploy verticle.
	 */
	@PostConstruct
	public void deployVerticle() {
		validatebean.deployVerticle();

	}
}
