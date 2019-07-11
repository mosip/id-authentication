package io.mosip.kernel.config.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server application
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigServerBootApplication {

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args args
	 */
	public static void main(String[] args) {
		SpringApplication.run(ConfigServerBootApplication.class, args);
	}
}
