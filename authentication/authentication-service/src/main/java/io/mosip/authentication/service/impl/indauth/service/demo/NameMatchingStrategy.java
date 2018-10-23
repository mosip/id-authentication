package io.mosip.authentication.service.impl.indauth.service.demo;

import io.mosip.authentication.core.util.MatcherUtil;

/**
 * 
 * @author Dinesh Karuppiah
 */

public enum NameMatchingStrategy implements MatchingStrategy {

	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeName((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeName((String) entityInfo);
			return MatcherUtil.doExactMatch(refInfoName, entityInfoName);
		} else {
			return 0;
		}
	}), PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeName((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeName((String) entityInfo);
			return MatcherUtil.doPartialMatch(refInfoName, entityInfoName);
		} else {
			return 0;
		}
	}), PHONETICS(MatchingStrategyType.PHONETICS, (Object reqInfo, Object entityInfo) -> 0);

	private final MatchFunction matchFunction;

	private final MatchingStrategyType matchStrategyType;

	/**
	 * Constructor for Name Matching Strategy
	 * 
	 * @param matchStrategyType
	 * @param matchFunction
	 */
	private NameMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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
