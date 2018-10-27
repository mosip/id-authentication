package io.mosip.registration.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import io.mosip.registration.core.generator.MosipGroupIdGenerator;

/*
 * (non-Javadoc)
 * 
 *
 */

@SpringBootApplication
@ComponentScan(basePackages="io.mosip.*")
@PropertySource({"classpath:core-application.properties"})
public class GroupidGeneratorApplication {

	@Autowired
	private MosipGroupIdGenerator<String> groupIdGenerator;

	public static void main(String[] args) {
		SpringApplication.run(GroupidGeneratorApplication.class, args);
	}
}
