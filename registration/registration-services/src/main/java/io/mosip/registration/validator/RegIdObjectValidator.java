package io.mosip.registration.validator;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
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
	private ObjectMapper mapper;

	@Autowired
	private RegIdObjectMasterDataValidator regIdObjectMasterDataValidator;

	/**
	 * This method validates the input object against the schema, mandatory, pattern
	 * and Master data using differnt validator. If any validation failure, the
	 * packet won't get created and user will be notified with the issue. The
	 * mandatory validation varies based on the user action [New/ Update UIN/ Lost
	 * UIN].
	 *
	 * @param idObject
	 *            the id object
	 * @param registrationCategory
	 *            the registration category
	 * @throws BaseCheckedException
	 *             the base checked exception
	 */
	public void validateIdObject(Object idObject, String registrationCategory) throws BaseCheckedException {
		LOGGER.info(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"Validating schema of Identity Object");
		IdObjectValidatorSupportedOperations operationType = null;

		try {
			if (registrationCategory.equalsIgnoreCase(RegistrationConstants.PACKET_TYPE_NEW)) {
				operationType = IdObjectValidatorSupportedOperations.NEW_REGISTRATION;
			} else if (registrationCategory.equalsIgnoreCase(RegistrationConstants.PACKET_TYPE_UPDATE)) {
				operationType = IdObjectValidatorSupportedOperations.UPDATE_UIN;
			} else if (registrationCategory.equalsIgnoreCase(RegistrationConstants.PACKET_TYPE_LOST)) {
				operationType = IdObjectValidatorSupportedOperations.LOST_UIN;
			} else if ((Boolean) SessionContext.map().get(RegistrationConstants.IS_Child)) {
				operationType = IdObjectValidatorSupportedOperations.CHILD_REGISTRATION;
			}

			if (idObjectValidator.validateIdObject(idObject, operationType)) {
				LOGGER.info(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
						"ID object shema validation is successful");
				if (idOjectPatternvalidator.validateIdObject(idObject, operationType)
						&& ageValidation(idObject, operationType)) {
					LOGGER.info(LoggerConstants.ID_OBJECT_PATTERN_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
							"ID object pattern validation is successful");
					if (regIdObjectMasterDataValidator.validateIdObject(idObject, operationType)) {
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
		} catch (JsonProcessingException jsonProcessingException) {
			LOGGER.error(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(jsonProcessingException));
			throw new RegBaseCheckedException("REG-PAV-001", "Registrtaion Pattern Validator for age",
					jsonProcessingException);
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(runtimeException));
			throw runtimeException;
		}
		LOGGER.info(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"Completed validating schema of Identity Object");

	}

	private boolean ageValidation(Object identityObject, IdObjectValidatorSupportedOperations operationType)
			throws JsonProcessingException {
		LOGGER.info(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"Completed validating age from global param starting");
		int maxAge = Integer.parseInt(
				ApplicationContext.getInstance().getApplicationMap().get(RegistrationConstants.MAX_AGE).toString());

		String identityString = mapper.writeValueAsString(identityObject);
		JsonParser jsonParser = new JsonParser();

		JsonElement jsonElement = jsonParser.parse(identityString);
		if (jsonElement.isJsonObject()
				&& jsonElement.getAsJsonObject().get(RegistrationConstants.AGE_IDENTITY).getAsJsonObject().get(RegistrationConstants.UIN_UPDATE_AGE) != null) {
			return maxAge >= jsonElement.getAsJsonObject().get(RegistrationConstants.AGE_IDENTITY).getAsJsonObject().get(RegistrationConstants.UIN_UPDATE_AGE).getAsInt();
		} else if (operationType.equals(IdObjectValidatorSupportedOperations.UPDATE_UIN)
				|| operationType.equals(IdObjectValidatorSupportedOperations.LOST_UIN)
				|| jsonElement.getAsJsonObject().get(RegistrationConstants.AGE_IDENTITY).getAsJsonObject().get(RegistrationConstants.DATE_OF_BIRTH) != null) {
			return true;
		}

		LOGGER.info(LoggerConstants.ID_OBJECT_SCHEMA_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"Completed validating age from global param ending ");
		return false;
	}

}
