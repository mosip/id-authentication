package io.mosip.authentication.core.util;

import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;

import java.util.logging.Logger;

/**
 * The Class DataValidationUtil - Checks for errors in the error object
 * and throws {@link IDDataValidationException}, if any error is present.
 *
 * @author Manoj SP
 */
public final class DataValidationUtil {

    private final static Logger logger = Logger.getLogger(DataValidationUtil.class.getName());

	/**
	 * Instantiates a new data validation util.
	 */
	private DataValidationUtil() {
	}

	/**
	 * Get list of errors from error object and throws
	 * {@link IDDataValidationException}, if any error is present.
	 *
	 * @param errors the errors
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public static void validate(Errors errors) throws IDDataValidationException {
		if (errors.hasErrors()) {
			IDDataValidationException exception = new IDDataValidationException();
			exception.clearArgs();
			errors.getAllErrors()
					.forEach(error -> {
						String errorCode = error.getCode();
                        logger.info("Validation Error Code: " + errorCode);
						String errorMessage = error.getDefaultMessage();
                        logger.info("Validation Error Message: " + errorMessage);
						String actionMesgsage = IdAuthenticationErrorConstants.getActionMessageForErrorCode(error.getCode()).orElse(null);
                        logger.info("Validation Action Message: " + actionMesgsage);
						Object[] args = error.getArguments();
						exception.addInfo(errorCode, errorMessage, actionMesgsage, args);
                        logger.info("EXCEPTION OCCURED : "+exception);
					});
			throw exception;
		}
	}

}
