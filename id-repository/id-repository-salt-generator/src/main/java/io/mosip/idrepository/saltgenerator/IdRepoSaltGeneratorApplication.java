package io.mosip.idrepository.saltgenerator;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Manoj SP
 *
 */
@SpringBootApplication
@EnableBatchProcessing
public class IdRepoSaltGeneratorApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(IdRepoSaltGeneratorApplication.class,
				args);
		SpringApplication.exit(applicationContext);
	}

}
