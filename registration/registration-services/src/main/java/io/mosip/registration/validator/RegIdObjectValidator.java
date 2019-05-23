package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idobjectvalidator.exception.FileIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectSchemaIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationProcessingException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 * The class to validate the Schema of Identity Object. This class internally
 * invokes the {@link IdObjectValidator} class provided by the kernel
 * module to validate the schema.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Service
public class RegIdObjectValidator {

	private static final Logger LOGGER = AppConfig.getLogger(RegIdObjectValidator.class);
	@Autowired
	@Qualifier("schema")
	private IdObjectValidator idObjectValidator;

	public boolean validateIdObject(Object idObject) {
		LOGGER.info(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"Validating schema of Identity Object");

		boolean isIdObjectSchemaValid = false;

		try {
			isIdObjectSchemaValid = idObjectValidator.validateIdObject(idObject);
		} catch (IdObjectValidationProcessingException | IdObjectIOException | IdObjectSchemaIOException
				| FileIOException idObjectValidatorException) {
			LOGGER.error(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(idObjectValidatorException));
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(runtimeException));
		}

		LOGGER.info(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"Completed validating schema of Identity Object");

		return isIdObjectSchemaValid;
	}

}
