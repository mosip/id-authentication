package io.mosip.registration.validator;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;

/**
 * The class to validate the Schema of Identity Object. This class internally
 * invokes the {@link IdObjectValidator} class provided by the kernel module to
 * validate the schema.
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

	@Autowired
	@Qualifier("pattern")
	private IdObjectValidator idOjectPatternvalidator;

	@Autowired
	private RegIdObjectMasterDataValidator regIdObjectMasterDataValidator;

	public void validateIdObject(Object idObject, IdObjectValidatorSupportedOperations registrationCategory)
			throws BaseCheckedException {
		LOGGER.info(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"Validating schema of Identity Object");

		try {
			if (idObjectValidator.validateIdObject(idObject, registrationCategory)) {
				LOGGER.info(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
						"ID object shema validation is successful");
				if (idOjectPatternvalidator.validateIdObject(idObject, registrationCategory)) {
					LOGGER.info(LoggerConstants.ID_OBJECT_PATTERN_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
							"ID object pattern validation is successful");
					if (regIdObjectMasterDataValidator.validateIdObject(idObject, registrationCategory)) {
						LOGGER.info(LoggerConstants.ID_OBJECT_PATTERN_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
								"ID object master data validation is successful");
					} else {
						throw new RegBaseCheckedException(
								RegistrationExceptionConstants.ID_OBJECT_MASTER_DATA_VALIDATOR.getErrorCode(),
								RegistrationExceptionConstants.ID_OBJECT_MASTER_DATA_VALIDATOR.getErrorMessage());
					}
				} else {
					throw new RegBaseCheckedException(
							RegistrationExceptionConstants.ID_OBJECT_PATTERN_VALIDATOR.getErrorCode(),
							RegistrationExceptionConstants.ID_OBJECT_PATTERN_VALIDATOR.getErrorMessage());
				}
			} else {
				throw new RegBaseCheckedException(
						RegistrationExceptionConstants.ID_OBJECT_SCHEMA_VALIDATOR.getErrorCode(),
						RegistrationExceptionConstants.ID_OBJECT_SCHEMA_VALIDATOR.getErrorMessage());
			}
		} catch (IdObjectValidationFailedException | IdObjectIOException idObjectValidatorException) {
			LOGGER.error(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(idObjectValidatorException));
			throw idObjectValidatorException;
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(runtimeException));
			throw runtimeException;
		}
		LOGGER.info(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"Completed validating schema of Identity Object");

	}

}
