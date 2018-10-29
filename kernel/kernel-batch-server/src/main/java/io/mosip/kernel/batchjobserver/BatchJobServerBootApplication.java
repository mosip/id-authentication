package io.mosip.kernel.batchjobserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.dataflow.server.EnableDataFlowServer;

/**
 * Main class for batch cloud server.
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@EnableDataFlowServer
@SpringBootApplication
public class BatchJobServerBootApplication {

	/**
	 * Main method for batch cloud server.
	 * 
	 * @param args
	 *            the argument.
	 */
	public static void main(String[] args) {
		SpringApplication.run(BatchJobServerBootApplication.class, args);
	}
}
