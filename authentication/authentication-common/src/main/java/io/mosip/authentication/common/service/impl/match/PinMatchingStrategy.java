package io.mosip.authentication.common.service.impl.match;

import java.util.Map;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.DemoMatcherUtil;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * The Enum PinMatchingStrategy - used to compare and
 * evaluate the PIN value received from the request and entity
 * 
 * @author Sanjay Murali
 */
public enum PinMatchingStrategy implements TextMatchingStrategy {

	/** The exact. */
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String hashPin = HMACUtils.digestAsPlainText(HMACUtils.generateHash(((String) reqInfo).getBytes()));
			return DemoMatcherUtil.doExactMatch(hashPin, (String) entityInfo);
		} else {
			logError(IdAuthenticationErrorConstants.PIN_MISMATCH);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PIN_MISMATCH);
		}
	});

	/** The match function. */
	private final MatchFunction matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/** The Constant Pin Matching strategy. */
	private static final String TYPE = "PinMatchingStrategy";

	/**
	 * Instantiates a new age matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction     the match function
	 */
	PinMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategy#
	 * getType()
	 */
	@Override
	public MatchingStrategyType getType() {
		return matchStrategyType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategy#
	 * getMatchFunction()
	 */
	@Override
	public MatchFunction getMatchFunction() {
		return matchFunction;
	}

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(PinMatchingStrategy.class);

	/**
	 * Log error.
	 *
	 * @param errorConstants the error constants
	 */
	private static void logError(IdAuthenticationErrorConstants errorConstants) {
		mosipLogger.error(IdAuthCommonConstants.SESSION_ID, TYPE, "Inside PinMatchingStrategy" + errorConstants.getErrorCode(),
				errorConstants.getErrorMessage());
	}

}
