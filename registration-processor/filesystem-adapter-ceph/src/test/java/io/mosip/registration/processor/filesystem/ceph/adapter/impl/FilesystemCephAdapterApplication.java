package io.mosip.registration.processor.filesystem.ceph.adapter.impl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
/**
 * The Class FilesystemCephAdapterApplication.
 */
@SpringBootApplication
@ComponentScan(basePackages="io.mosip.registration.*")
public class FilesystemCephAdapterApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(FilesystemCephAdapterApplication.class, args);
	}
}
