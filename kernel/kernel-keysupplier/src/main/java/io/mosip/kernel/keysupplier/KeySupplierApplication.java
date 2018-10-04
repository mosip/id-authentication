package io.mosip.kernel.keysupplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Urvil Joshi
 *
 * @Since 1.0.0
 */
@SpringBootApplication
@PropertySource(value = {"application.properties"})
public class KeySupplierApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeySupplierApplication.class, args);
	}
}
