package io.mosip.kernel.masterdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class of Master-Data-Service Application. This will have CRUD operations
 * related to master data
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.kernel.masterdata.*", "io.mosip.kernel.auth.*" })
public class MasterDataBootApplication {

	/**
	 * Function to run the Master-Data-Service application
	 * 
	 * @param args The arguments to pass will executing the main function
	 */
	public static void main(String[] args) {
		SpringApplication.run(MasterDataBootApplication.class, args);
	}

}