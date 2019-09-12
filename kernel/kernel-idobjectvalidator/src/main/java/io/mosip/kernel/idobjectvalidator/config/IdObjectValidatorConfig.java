package io.mosip.kernel.idobjectvalidator.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
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

	/**
	 * Reference validator.
	 *
	 * @return the id object validator
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	@Bean
	@Lazy
	public IdObjectValidator referenceValidator()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (StringUtils.isNotBlank(env.getProperty("mosip.kernel.idobjectvalidator.referenceValidator"))) {
			return (IdObjectValidator) Class
					.forName(env.getProperty("mosip.kernel.idobjectvalidator.referenceValidator")).newInstance();
		} else {
			
			return new IdObjectValidator() {

				@Override
				public boolean validateIdObject(Object arg0, IdObjectValidatorSupportedOperations arg1)
						throws IdObjectValidationFailedException, IdObjectIOException {
					return true;
				}
			};
		}
	}
}
