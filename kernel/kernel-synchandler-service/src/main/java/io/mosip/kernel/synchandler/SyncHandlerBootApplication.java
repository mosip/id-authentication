package io.mosip.kernel.synchandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Main class of Sync handler Application.
 * 
 * @author Abhishek Kumar
 * @since 29-11-2018
 */
@SpringBootApplication
@EnableAspectJAutoProxy
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

	/**
	 * @Bean CommandLineRunner runner(MasterDataServiceImpl service) { return args
	 *       -> { service.getDevices("1",
	 *       LocalDateTime.parse("2018-11-01T01:01:01")).stream().map(DeviceDto::getName).forEach(System.out::println);
	 *       }; }
	 */

}
