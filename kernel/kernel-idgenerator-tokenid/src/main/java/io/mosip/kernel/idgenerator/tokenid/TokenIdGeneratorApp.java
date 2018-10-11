package io.mosip.kernel.idgenerator.tokenid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.kernel.idgenerator.tokenid.generator.TokenIdGenerator;

/**
 * This application will generate token Id in random order.
 * 
 * @author M1046464
 *
 */
@SpringBootApplication
public class TokenIdGeneratorApp   {

	@Autowired
	TokenIdGenerator tokenGen;

	

	public static void main(String[] args) {
		SpringApplication.run(TokenIdGeneratorApp.class, args);
	}



	

	

}
