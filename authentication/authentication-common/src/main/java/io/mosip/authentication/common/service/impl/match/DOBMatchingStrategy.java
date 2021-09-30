package io.mosip.authentication.common.service.impl.match;

import java.util.Date;
import java.util.Map;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
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
 * The Enum DOBMatchingStrategy - used to compare and evaluate the DOB value
 * received from the request and entity
 *
 * @author Sanjay Murali
 * @author Nagarjuna
 */
public enum DOBMatchingStrategy implements TextMatchingStrategy {

	/** The exact. */
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			Date reqInfoDate;
			String dateOfBirthFormat = getDateOfBirthFormat(props);
			try {
				reqInfoDate = DateUtils.parseToDate((String) reqInfo, dateOfBirthFormat);
			} catch (ParseException e) {
				logError(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER);
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								"request/demographics/" + IdaIdMapping.DOB.getIdname()),
						e);
			}
			
			Date entityInfoDate;
			try {
				entityInfoDate = DateUtils.parseToDate((String) entityInfo, dateOfBirthFormat);
			} catch (ParseException e) {
				logError(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED);
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
			}
			
			return getDemoMatcherUtilObject(props).doExactMatch(reqInfoDate, entityInfoDate);

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
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction     the match function
	 */
	DOBMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	private static String getDateOfBirthFormat(Map<String, Object> props) {
		Object envObj = props.get("env");
		String dobFormat;
		if(envObj instanceof Environment) {
			Environment env = (Environment) envObj;
			dobFormat = env.getProperty(IdAuthConfigKeyConstants.MOSIP_DATE_OF_BIRTH_PATTERN, IdAuthCommonConstants.DEFAULT_DOB_PATTERN);
		} else {
			dobFormat =  IdAuthCommonConstants.DEFAULT_DOB_PATTERN;
		}
		return dobFormat;
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

	/** The Constant DOB Matching strategy. */
	private static final String TYPE = "DOBMatchingStrategy";

	/**
	 * Log error.
	 *
	 * @param errorConstants the error constants
	 */
	private static void logError(IdAuthenticationErrorConstants errorConstants) {
		mosipLogger.error(IdAuthCommonConstants.SESSION_ID, TYPE,
				"Inside DOB Mathing Strategy" + errorConstants.getErrorCode(), errorConstants.getErrorMessage());
	}

	/**
	 * Gets the demoMatcherUtil object
	 * @param props
	 * @return
	 */
	public static DemoMatcherUtil getDemoMatcherUtilObject(Map<String, Object> props) {
		return (DemoMatcherUtil)props.get("demoMatcherUtil");
	}
}
