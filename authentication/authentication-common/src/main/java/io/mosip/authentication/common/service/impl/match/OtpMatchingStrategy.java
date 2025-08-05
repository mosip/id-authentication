package io.mosip.authentication.common.service.impl.match;

import java.util.Map;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.ValidateOtpFunction;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Matching Strategy for OTP
 *
 * @author Dinesh Karuppiah.T
 */
public enum OtpMatchingStrategy implements TextMatchingStrategy {

	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		// Log the start of EXACT OTP matching strategy
		logInfo("Starting EXACT OTP matching strategy");

		// Fetching IDVID from props
		logInfo("Fetching IDVID from properties");
		Object idvidObj = props.get(IdAuthCommonConstants.IDVID);

		// Checking input types
		logInfo(String.format("Checking input types: reqInfo=%s, entityInfo=%s, idvidObj=%s",
				reqInfo != null ? reqInfo.getClass().getSimpleName() : "null",
				entityInfo != null ? entityInfo.getClass().getSimpleName() : "null",
				idvidObj != null ? idvidObj.getClass().getSimpleName() : "null"));
		if (reqInfo instanceof String && entityInfo instanceof String && idvidObj instanceof String) {
			// Fetching ValidateOtpFunction
			logInfo("Fetching ValidateOtpFunction from properties");
			Object object = props.get(ValidateOtpFunction.class.getSimpleName());

			// Checking if ValidateOtpFunction is valid
			logInfo(String.format("Checking ValidateOtpFunction type: object=%s",
					object != null ? object.getClass().getSimpleName() : "null"));
			if (object instanceof ValidateOtpFunction) {
				// Casting and validating OTP
				logInfo("Casting object to ValidateOtpFunction and validating OTP");
				ValidateOtpFunction func = (ValidateOtpFunction) object;
				boolean otpValid = func.validateOtp((String) reqInfo, (String) entityInfo, (String) idvidObj);

				// Checking OTP validation result
				logInfo(String.format("OTP validation result for IDVID=%s: %s", idvidObj, otpValid));
				if (!otpValid) {
					logInfo("OTP validation failed, returning 0");
					return 0;
				} else {
					logInfo("OTP validation successful, returning 100");
					return 100;
				}
			} else {
				logInfo("Invalid ValidateOtpFunction object, logging error");
				logError(IdAuthenticationErrorConstants.INVALID_OTP);
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								PinAuthType.OTP.getType()));
			}
		} else {
			logInfo("Invalid input types for OTP validation, returning 0");
			return 0;
		}
	});

	/**
	 * Otp Matching Strategy
	 */
	private static final String TYPE = "OtpMatchingStrategy";

	/** The match function. */
	private final MatchFunction matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/** The mosipLogger. */
	private static final Logger mosipLogger = IdaLogger.getLogger(OtpMatchingStrategy.class);

	/*
	 * Matching Strategy Type
	 *
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#getType()
	 */
	@Override
	public MatchingStrategyType getType() {
		return matchStrategyType;
	}

	/*
	 * Get MatchFunction
	 *
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#getMatchFunction()
	 */
	@Override
	public MatchFunction getMatchFunction() {
		return matchFunction;
	}

	/**
	 * Constructor
	 *
	 * @param matchStrategyType
	 * @param matchFunction
	 */
	OtpMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	/**
	 * Log error message
	 *
	 * @param errorConstants
	 */
	private static void logError(IdAuthenticationErrorConstants errorConstants) {
		mosipLogger.error(IdAuthCommonConstants.SESSION_ID, TYPE,
				"Error in OtpMatchingStrategy: " + errorConstants.getErrorCode(),
				errorConstants.getErrorMessage());
	}

	/**
	 * Log info message
	 *
	 * @param message
	 */
	private static void logInfo(String message) {
		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, TYPE, message);
		System.out.println(message); // For demonstration purposes
	}
}
