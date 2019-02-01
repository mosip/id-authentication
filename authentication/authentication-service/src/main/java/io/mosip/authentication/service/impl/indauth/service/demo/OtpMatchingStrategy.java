package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Map;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.ValidateOtpFunction;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * @author M1047697
 *
 */
public enum OtpMatchingStrategy implements TextMatchingStrategy {

	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			Object object = props.get(ValidateOtpFunction.class.getSimpleName());
			if (object instanceof ValidateOtpFunction) {
				ValidateOtpFunction func = (ValidateOtpFunction) object;
				boolean otpValid = func.validateOtp((String) reqInfo, (String) entityInfo);
				return otpValid ? 100 : 0;
			} else {
				logError(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
			}
		} else {
			logError(IdAuthenticationErrorConstants.INVALID_OTP);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_OTP);
		}
	});

	/**
	 * Session id
	 */
	private final static String DEFAULT_SESSION_ID = "sessionId";

	/**
	 * Otp Matching Strategy
	 */
	private final static String TYPE = "OtpMatching Strategy";

	/** The match function. */
	private final MatchFunction matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OtpMatchingStrategy.class);

	/*
	 * Matching Strategy Type
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#getType()
	 */
	@Override
	public MatchingStrategyType getType() {
		return matchStrategyType;
	}

	private static void logError(IdAuthenticationErrorConstants errorConstants) {
		mosipLogger.error(DEFAULT_SESSION_ID, TYPE,
				"Inside OtpMatchingStrategy Strategy" + errorConstants.getErrorCode(),
				errorConstants.getErrorMessage());
	}

	/*
	 *
	 * Get MatchFunction
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#
	 * getMatchFunction()
	 */
	@Override
	public MatchFunction getMatchFunction() {
		return matchFunction;
	}

	/**
	 * @param matchStrategyType
	 * @param matchFunction
	 */
	OtpMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

}
