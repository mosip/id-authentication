/**
 * 
 */
package io.mosip.kernel.cbeffutil.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Ramadurai Pandian
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.kernel.*")
public class KernelCbeffUtilApplication {
	public static void main(String[] args) {
		SpringApplication.run(KernelCbeffUtilApplication.class, args);

	}
}
