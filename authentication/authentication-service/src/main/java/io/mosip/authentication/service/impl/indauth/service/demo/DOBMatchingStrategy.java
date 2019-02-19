package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Date;
import java.util.Map;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;
import io.mosip.authentication.core.util.DemoMatcherUtil;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Enum DOBMatchingStrategy.
 *
 * @author Sanjay Murali
 */
public enum DOBMatchingStrategy implements TextMatchingStrategy {

	/** The exact. */
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			try {
				Environment env = (Environment) props.get("env");
				String dateFormatReq = env.getProperty("dob.req.date.pattern");
				String dateFormatEntity = env.getProperty("dob.entity.date.pattern");
				Date reqInfoDate = DateUtils.parseToDate((String) reqInfo, dateFormatReq);
				Date entityInfoDate = DateUtils.parseToDate((String) entityInfo, dateFormatEntity);
				return DemoMatcherUtil.doExactMatch(reqInfoDate, entityInfoDate);
			} catch ( ParseException | java.text.ParseException e) {
				logError(IdAuthenticationErrorConstants.DOB_MISMATCH);
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DOB_MISMATCH, e);
			}
		}
		return 0;
	});

	/** The match function. */
	private final MatchFunction matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/**
	 * Instantiates a new DOB matching strategy.
	 *
	 * @param matchStrategyType
	 *            the match strategy type
	 * @param matchFunction
	 *            the match function
	 */
	DOBMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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
	private static Logger mosipLogger = IdaLogger.getLogger(DOBMatchingStrategy.class);

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

	/** The Constant DOB Matching strategy. */
	private static final String TYPE = "DOBMatchingStrategy";

	/**
	 * Log error.
	 *
	 * @param errorConstants
	 *            the error constants
	 */
	private static void logError(IdAuthenticationErrorConstants errorConstants) {
		mosipLogger.error(DEFAULT_SESSION_ID, TYPE, "Inside DOB Mathing Strategy" + errorConstants.getErrorCode(),
				errorConstants.getErrorMessage());
	}

}
