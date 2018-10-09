package io.mosip.authentication.service.impl.indauth.service.demo;

import io.mosip.authentication.core.util.MatcherUtil;

/**
 * 
 * @author Dinesh Karuppiah
 */

public enum NameMatchingStrategy implements MatchingStrategy {

	EXACT(MatchStrategyType.EXACT, (reqInfo, entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeName((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeName((String) entityInfo);
			return MatcherUtil.doExactMatch(refInfoName, entityInfoName);
		} else {
			return 0;
		}
	}), PARTIAL(MatchStrategyType.PARTIAL, (reqInfo, entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeName((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeName((String) entityInfo);
			return MatcherUtil.doPartialMatch(refInfoName, entityInfoName);
		} else {
			return 0;
		}
	}), PHONETICS(MatchStrategyType.PHONETICS, (reqInfo, entityInfo) -> 0);

	private final MatchFunction matchFunction;

	private final MatchStrategyType matchStrategyType;

	/**
	 * 
	 * @param matchStrategyType
	 * @param matchValue
	 * @param matchFunction
	 */
	private NameMatchingStrategy(MatchStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	@Override
	public MatchStrategyType getType() {
		return matchStrategyType;
	}

	@Override
	public MatchFunction getMatchFunction() {
		return matchFunction;
	}

}
