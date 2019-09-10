package io.mosip.kernel.idobjectvalidator.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;

/**
 * The Class IdObjectValidatorConfig.
 *
 * @author Manoj SP
 */
@Configuration
public class IdObjectValidatorConfig {

	/** The env. */
	@Autowired
	private Environment env;

	@Bean
	public IdObjectValidator referenceValidator()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (StringUtils.isNotBlank(env.getProperty("mosip.kernel.idobjectvalidator.referenceValidator"))) {
			return (IdObjectValidator) Class
					.forName(env.getProperty("mosip.kernel.idobjectvalidator.referenceValidator")).newInstance();
		}
		return null;
	}
}
