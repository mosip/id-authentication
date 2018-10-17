package io.mosip.registration.exception;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class for handling the REG unchecked exception
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class RegBaseUncheckedException extends BaseUncheckedException {

	/**
	 * Serializable Version Id
	 */
	private static final long serialVersionUID = 276197701640260133L;
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}
	
	/**
	 * Constructs a new unchecked exception
	 */
	public RegBaseUncheckedException() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param errorCode
	 *            the Error Code Corresponds to Particular Exception
	 * @param errorMessage
	 *            the Message providing the specific context of the error
	 */
	public RegBaseUncheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		LOGGER.error("REGISTRATION - UNCHECKED_EXCEPTION", APPLICATION_NAME,
				APPLICATION_ID, errorCode + "-->" + errorMessage);
	}

	/**
	 * Constructor
	 * 
	 * @param errorCode
	 *            the Error Code Corresponds to Particular Exception
	 * @param errorMessage
	 *            the Message providing the specific context of the error
	 * @param throwable
	 *            the Cause of exception
	 */
	public RegBaseUncheckedException(String errorCode, String errorMessage, Throwable throwable) {
		super(errorCode, errorMessage, throwable);
		LOGGER.error("REGISTRATION - UNCHECKED_EXCEPTION", APPLICATION_NAME,
				APPLICATION_ID, errorCode + "-->" + errorMessage);
	}
}
