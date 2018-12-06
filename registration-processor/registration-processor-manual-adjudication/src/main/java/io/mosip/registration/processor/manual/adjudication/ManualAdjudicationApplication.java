package io.mosip.registration.processor.manual.adjudication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ManualAdjudicationApplication Main class
 * 
 * @author Pranav Kumar
 * @since 0.0.1
 */
@SpringBootApplication(scanBasePackages = {"io.mosip.registration.processor.packet.receiver","io.mosip.registration.processor.status","io.mosip.registration.processor.rest.client", "io.mosip.registration.processor.manual.adjudication", "io.mosip.registration.processor.filesystem.ceph.adapter.impl"})
public class ManualAdjudicationApplication {

	/**
	 * Main method to instantiate the spring boot application
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ManualAdjudicationApplication.class, args);
	}
}
