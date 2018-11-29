package io.mosip.kernel.synchandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class of Sync handler Application. 
 * 
 * @author Abhishek Kumar
 * @since 29-11-2018
 */
@SpringBootApplication
public class SyncHandlerBootApplication {
	/**
	 * Function to run the Master-Data-Service application
	 * 
	 * @param args
	 *            The arguments to pass will executing the main function
	 */
	public static void main(String[] args) {
		SpringApplication.run(SyncHandlerBootApplication.class, args);
	}

}
