package io.mosip.preregistration.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import io.mosip.preregistration.core.generator.MosipGroupIdGenerator;

/*
 * (non-Javadoc)
 * 
 *
 */

@SpringBootApplication
@ComponentScan(basePackages="io.mosip.*")
public class GroupidGeneratorApplication {

	@Autowired
	private MosipGroupIdGenerator<String> groupIdGenerator;

	public static void main(String[] args) {
		SpringApplication.run(GroupidGeneratorApplication.class, args);
	}
}
