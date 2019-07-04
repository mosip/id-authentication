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
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Enum AgeMatchingStrategy - used to compare and
 * evaluate the AGE value received from the request and entity
 *
 * @author Sanjay Murali
 */
public enum AgeMatchingStrategy implements TextMatchingStrategy {

	/** The exact. */
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		try {
			int reqAge = Integer.parseInt(String.valueOf(reqInfo));
			int entityAge = Integer.parseInt(String.valueOf(entityInfo));
			return DemoMatcherUtil.doLessThanEqualToMatch(reqAge, entityAge);
		} catch (NumberFormatException e) {
			logError(e);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		}
	});

	/** The match function. */
	private final MatchFunction matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(AgeMatchingStrategy.class);


	/** The Constant AGE Matching strategy. */
	private static final String TYPE = "AgeMatchingStrategy";

	/**
	 * Instantiates a new age matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction     the match function
	 */
	AgeMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	/**
	 * Log error.
	 *
	 * @param e the e
	 */
	private static void logError(NumberFormatException e) {
		mosipLogger.error(IdAuthCommonConstants.SESSION_ID, TYPE, "Inside AgeMathing Strategy", ExceptionUtils.getStackTrace(e));
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

}
