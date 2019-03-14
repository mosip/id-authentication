package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Map;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;
import io.mosip.authentication.core.util.DemoMatcherUtil;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;

/**
 * The Enum AddressMatchingStrategy.
 *
 * @author Dinesh Karuppiah.T
 */
public enum AddressMatchingStrategy implements TextMatchingStrategy {

	/** The exact. */
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeAddress((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeAddress((String) entityInfo);
			return DemoMatcherUtil.doExactMatch(refInfoName, entityInfoName);
		} else {
			Object object = props.get("languageType");
			if (object instanceof LanguageType) {
				LanguageType langType = ((LanguageType) object);
				if (langType.equals(LanguageType.PRIMARY_LANG)) {
					throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.DEMO_DATA_MISMATCH.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.DEMOGRAPHIC_DATA_MISMATCH.getErrorMessage(),
									getLanguagecode(LanguageType.PRIMARY_LANG), DemoAuthType.ADDRESS.getType()));
				} else {
					throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.DEMO_DATA_MISMATCH.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.DEMOGRAPHIC_DATA_MISMATCH.getErrorMessage(),
									getLanguagecode(LanguageType.PRIMARY_LANG), DemoAuthType.ADDRESS.getType()));
				}
			} else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
			}
		}
	});

	/** The match function. */
	private final MatchFunction matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/**
	 * Constructor for Address Matching Strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction     the match function
	 */
	AddressMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	private static String getLanguagecode(LanguageType primaryLang) {
		return primaryLang.PRIMARY_LANG.toString();
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
