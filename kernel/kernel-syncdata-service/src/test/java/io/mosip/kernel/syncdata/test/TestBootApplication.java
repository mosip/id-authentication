package io.mosip.kernel.syncdata.test;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.mosip.kernel.syncdata.test.config.TestSecurityConfig;

/**
 * Main class of Sync handler Application.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "io.mosip.kernel.syncdata.*")
@EnableAsync
@Import(TestSecurityConfig.class)
public class TestBootApplication {
	/**
	 * Function to run the Master-Data-Service application
	 * 
	 * @param args The arguments to pass will executing the main function
	 */
	public static void main(String[] args) {
		SpringApplication.run(TestBootApplication.class, args);
	}

	/**
	 * Creating bean of TaskExecutor to run Async tasks
	 * 
	 * @return {@link Executor}
	 */
	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(15);
		executor.setMaxPoolSize(30);
		executor.setThreadNamePrefix("SYNCDATA-Async-Thread-");
		executor.initialize();
		return executor;
	}
}
