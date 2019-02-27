package io.mosip.kernel.idrepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Import;

import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtils;
import io.mosip.kernel.idvalidator.rid.impl.RidValidatorImpl;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;

/**
 * The Class IdRepoApplication.
 *
 * @author Manoj SP
 */
@SpringBootApplication(exclude = MailSenderAutoConfiguration.class)
@Import(value = { UinValidatorImpl.class, RidValidatorImpl.class, JsonValidatorImpl.class, ConnectionUtils.class })
public class IdRepoBootApplication {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(IdRepoBootApplication.class, args);
	}
}
