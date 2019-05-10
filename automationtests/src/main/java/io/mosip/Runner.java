package io.mosip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Runner {

	
	public static void main(String[] args) {
		System.out.println("starting ====");
		SpringApplication.run(Runner.class, args);
		System.out.println("ending ====");
	}
}
