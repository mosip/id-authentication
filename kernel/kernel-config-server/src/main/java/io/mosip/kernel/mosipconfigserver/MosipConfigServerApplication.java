package io.mosip.kernel.mosipconfigserver;


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
public class MosipConfigServerApplication {
  
	/**
	 * Main method to run spring boot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(MosipConfigServerApplication.class, args);
	}
}
