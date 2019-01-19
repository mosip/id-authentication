package io.mosip.registration.exception;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;

/**
 * The class to handle all the checked exception in REG
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegBaseCheckedException extends BaseCheckedException {

	/**
	 * Serializable Version Id
	 */
	private static final long serialVersionUID = 7381314129809012005L;
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegBaseCheckedException.class);

	/**
	 * Constructs a new checked exception
	 */
	public RegBaseCheckedException() {
		super();
	}

	/**
	 * Constructs a new checked exception with the specified detail message and
	 * error code.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the detail message.
	 */
	public RegBaseCheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		LOGGER.error("REGISTRATION - CHECKED_EXCEPTION", APPLICATION_NAME,
				APPLICATION_ID, errorCode + "-->" + errorMessage);
	}

	/**
	 * Constructs a new checked exception with the specified detail message and
	 * error code.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the detail message
	 * @param throwable
	 *            the specified cause
	 */
	public RegBaseCheckedException(String errorCode, String errorMessage, Throwable throwable) {
		super(errorCode, errorMessage, throwable);
		LOGGER.error("REGISTRATION - CHECKED_EXCEPTION", APPLICATION_NAME,
				APPLICATION_ID, errorCode + "-->" + errorMessage);
	}
}
