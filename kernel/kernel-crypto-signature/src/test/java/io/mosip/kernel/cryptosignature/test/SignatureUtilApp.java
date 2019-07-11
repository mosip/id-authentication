package io.mosip.kernel.cryptosignature.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "io.mosip.kernel.*" })
public class SignatureUtilApp {

	public static void main(String[] args) {
		SpringApplication.run(SignatureUtilApp.class, args);

	}
}
