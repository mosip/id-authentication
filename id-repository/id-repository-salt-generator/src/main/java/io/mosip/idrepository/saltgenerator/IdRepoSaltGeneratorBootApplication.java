package io.mosip.idrepository.saltgenerator;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author Manoj SP
 *
 */
@SpringBootApplication
@EnableBatchProcessing
public class IdRepoSaltGeneratorBootApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(IdRepoSaltGeneratorBootApplication.class,
				args);
		SpringApplication.exit(applicationContext);
	}

}
