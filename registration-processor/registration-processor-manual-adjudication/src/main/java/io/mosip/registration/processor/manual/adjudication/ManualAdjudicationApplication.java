package io.mosip.registration.processor.manual.adjudication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Class ManualAdjudicationApplication
 */
@SpringBootApplication(scanBasePackages = {"io.mosip.registration.processor.packet.receiver", "io.mosip.registration.processor.manual.adjudication", "io.mosip.registration.processor.filesystem.ceph.adapter.impl"})
public class ManualAdjudicationApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ManualAdjudicationApplication.class, args);
	}
}
