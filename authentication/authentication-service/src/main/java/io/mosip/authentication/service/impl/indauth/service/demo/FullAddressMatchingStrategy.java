package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.function.ToIntBiFunction;

import io.mosip.authentication.core.util.MatcherUtil;

public enum FullAddressMatchingStrategy implements MatchingStrategy {

	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeAddress((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeAddress((String) entityInfo);
			return MatcherUtil.doExactMatch(refInfoName, entityInfoName);
		} else {
			return 0;
		}
	}), PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeAddress((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeAddress((String) entityInfo);
			return MatcherUtil.doPartialMatch(refInfoName, entityInfoName);
		} else {
			return 0;
		}
	}), PHONETICS(MatchingStrategyType.PHONETICS, (reqInfo, entityInfo) -> 0);

	private final ToIntBiFunction<Object, Object> matchFunction;

	private final MatchingStrategyType matchStrategyType;

	/**
	 * Constructor for Full Address Matching Strategy
	 * 
	 * @param matchStrategyType
	 * @param matchFunction
	 */
	FullAddressMatchingStrategy(MatchingStrategyType matchStrategyType, ToIntBiFunction<Object, Object> matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	@Override
	public MatchingStrategyType getType() {
		return matchStrategyType;
	}

	@Override
	public ToIntBiFunction<Object, Object> getMatchFunction() {
		return matchFunction;
	}
}
