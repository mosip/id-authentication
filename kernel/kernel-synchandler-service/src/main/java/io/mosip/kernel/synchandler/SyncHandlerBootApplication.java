package io.mosip.kernel.synchandler;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Main class of Sync handler Application.
 * 
 * @author Abhishek Kumar
 * @since 29-11-2018
 */
@SpringBootApplication
@EnableAsync
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

	/*
	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(7);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("MasterData-Fetcher-");
		executor.initialize();
		return executor;
	}
	*/
	

}
