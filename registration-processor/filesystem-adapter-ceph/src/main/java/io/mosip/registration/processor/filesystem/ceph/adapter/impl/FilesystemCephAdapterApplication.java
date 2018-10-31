package io.mosip.registration.processor.filesystem.ceph.adapter.impl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({ "classpath:filesystem-application.properties" })
public class FilesystemCephAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilesystemCephAdapterApplication.class, args);
	}
}
