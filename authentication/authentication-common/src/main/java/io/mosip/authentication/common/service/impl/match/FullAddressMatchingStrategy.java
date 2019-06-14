package io.mosip.authentication.common.service.impl.match;

import java.util.Map;

import io.mosip.authentication.core.dto.DemoMatcherUtil;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;

/**
 * 
 * Matching Strategy for Full Address entity
 * 
 * @author Dinesh Karuppiah.T
 */

public enum FullAddressMatchingStrategy implements TextMatchingStrategy {

	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeAddress((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeAddress((String) entityInfo);
			return DemoMatcherUtil.doExactMatch(refInfoName, entityInfoName);
		} else {
			return 0;
		}

	}), PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeAddress((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeAddress((String) entityInfo);
			return DemoMatcherUtil.doPartialMatch(refInfoName, entityInfoName);
		} else {
			return 0;
		}
	}), PHONETICS(MatchingStrategyType.PHONETICS, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeAddress((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeAddress((String) entityInfo);
			String language = (String) props.get("language");
			return DemoMatcherUtil.doPhoneticsMatch(refInfoName, entityInfoName, language);
		} else {
			return 0;
		}
	});
	private final MatchFunction matchFunction;

	private final MatchingStrategyType matchStrategyType;

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

	@Override
	public MatchingStrategyType getType() {
		return matchStrategyType;
	}

	@Override
	public MatchFunction getMatchFunction() {
		return matchFunction;
	}
}
