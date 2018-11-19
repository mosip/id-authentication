package io.kernel.core.idrepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;

/**
 * @author Manoj SP
 *
 */
@SpringBootApplication
@Import(value = { UinValidatorImpl.class })
public class IdRepoApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdRepoApplication.class, args);
	}
}
