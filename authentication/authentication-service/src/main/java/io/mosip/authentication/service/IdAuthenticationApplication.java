package io.mosip.authentication.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Spring-boot class for ID Authentication Application.
 *
 * @author Dinesh Karuppiah
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.*", excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "io.mosip.kernel.datamapper.orika.*"))
public class IdAuthenticationApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(IdAuthenticationApplication.class, args);
	}

}
