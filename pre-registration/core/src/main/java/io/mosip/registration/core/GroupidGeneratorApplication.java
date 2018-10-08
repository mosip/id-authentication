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
@PropertySource({"classpath:application.properties"})
public class GroupidGeneratorApplication implements CommandLineRunner {

	@Autowired
	private MosipGroupIdGenerator<String> groupIdGenerator;

	public static void main(String[] args) {
		SpringApplication.run(GroupidGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n\n Generated Groupid : " + groupIdGenerator.generateGroupId() + "\n\n");
	}
}
