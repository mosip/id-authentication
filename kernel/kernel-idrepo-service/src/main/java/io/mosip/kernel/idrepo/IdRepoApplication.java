package io.mosip.kernel.idrepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;
import io.mosip.kernel.idvalidator.rid.impl.RidValidatorImpl;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

/**
 * The Class IdRepoApplication.
 *
 * @author Manoj SP
 */
@SpringBootApplication
@Import(value = { UinValidatorImpl.class, RidValidatorImpl.class, JsonValidatorImpl.class, EncryptorImpl.class, DecryptorImpl.class, KeyGenerator.class })
public class IdRepoApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(IdRepoApplication.class, args);
	}
}
