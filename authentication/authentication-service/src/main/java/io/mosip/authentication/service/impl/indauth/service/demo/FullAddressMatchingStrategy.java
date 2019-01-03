package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Map;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.util.DemoMatcherUtil;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public enum FullAddressMatchingStrategy implements MatchingStrategy {

	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeAddress((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeAddress((String) entityInfo);
			return DemoMatcherUtil.doExactMatch(refInfoName, entityInfoName);
		} else {
			return throwError(props);
		}

	}), PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeAddress((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeAddress((String) entityInfo);
			return DemoMatcherUtil.doPartialMatch(refInfoName, entityInfoName);
		} else {
			return throwError(props);
		}
	}), PHONETICS(MatchingStrategyType.PHONETICS, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeAddress((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeAddress((String) entityInfo);
			String language = (String) props.get("language");
			return DemoMatcherUtil.doPhoneticsMatch(refInfoName, entityInfoName, language);
		} else {
			return throwError(props);
		}
	});
	private final MatchFunction matchFunction;

	private final MatchingStrategyType matchStrategyType;

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(FullAddressMatchingStrategy.class);

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

	/** The Constant AGE Matching strategy. */
	private static final String TYPE = "FullAddressMatchingStrategy";

	/**
	 * Constructor for Full Address Matching Strategy
	 * 
	 * @param matchStrategyType
	 * @param matchFunction
	 */
	FullAddressMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	private static int throwError(Map<String, Object> props) throws IdAuthenticationBusinessException {
		final Object object = props.get("languageType");
		if (object instanceof LanguageType) {
			LanguageType langType = ((LanguageType) object);
			if (langType.equals(LanguageType.PRIMARY_LANG)) {
				logError(IdAuthenticationErrorConstants.FAD_PRI_MISMATCH);
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FAD_PRI_MISMATCH);
			} else {
				logError(IdAuthenticationErrorConstants.FAD_SEC_MISMATCH);
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FAD_SEC_MISMATCH);
			}
		} else {
			logError(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
		}
	}

	private static void logError(IdAuthenticationErrorConstants idAuthenticationErrorConstants) {
		mosipLogger.error(DEFAULT_SESSION_ID, TYPE,
				"Inside FullAddressMatchingStrategy Strategy" + idAuthenticationErrorConstants.getErrorCode(),
				idAuthenticationErrorConstants.getErrorMessage());
	}

	@Override
	public MatchingStrategyType getType() {
		return matchStrategyType;
	}

	@Override
	public MatchFunction getMatchFunction() {
		return matchFunction;
	}
}
