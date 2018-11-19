package io.mosip.kernel.keymanagerservice;

import java.security.Key;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.kernel.core.keymanager.spi.KeymanagerInterface;

/**
 * Key Manager Application
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@SpringBootApplication
public class KeymanagerBootApplication {

	@Autowired
	KeymanagerInterface keymanagerInterface;

	/**
	 * Main method to run spring boot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		SpringApplication.run(KeymanagerBootApplication.class, args);
	}

	@PostConstruct
	public void init() {
		List<String> allAlias = keymanagerInterface.getAllAlias();

		allAlias.forEach(alias -> {
			Key key = keymanagerInterface.getKey(alias);
			System.out.println(alias + "," + key);
			// keymanagerInterface.deleteKey(alias);
		});
	}
}
