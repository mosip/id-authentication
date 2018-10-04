package io.mosip.mosipconfigserver;

import org.eclipse.jgit.errors.TransportException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 
 * @author Swati Raj
 *
 */
@EnableConfigServer
@SpringBootApplication
public class MosipConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MosipConfigServerApplication.class, args);
	}
}
