package io.mosip.kernel.idgenerator.tokenid.test;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * (non-Javadoc)
 * PridGenerator Boot Application for SpringBootTest
 */

@SpringBootApplication
@ComponentScan(basePackages = "io.mosip.kernel.*")
public class TokenIdGeneratorBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(TokenIdGeneratorBootApplication.class, args);
	}
}

