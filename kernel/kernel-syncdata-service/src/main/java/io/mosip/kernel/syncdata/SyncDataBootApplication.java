package io.mosip.kernel.syncdata;

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
 * @since 1.0.0
 */
@SpringBootApplication
@EnableAsync
public class SyncDataBootApplication {
	/**
	 * Function to run the Master-Data-Service application
	 * 
	 * @param args
	 *            The arguments to pass will executing the main function
	 */
	public static void main(String[] args) {
		SpringApplication.run(SyncDataBootApplication.class, args);
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(15);
		executor.setMaxPoolSize(30);
		executor.setThreadNamePrefix("SYNCHANDLER-Async-Thread-");
		executor.initialize();
		return executor;
	}
}
