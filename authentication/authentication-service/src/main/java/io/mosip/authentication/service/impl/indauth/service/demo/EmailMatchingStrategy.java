package io.mosip.authentication.service.impl.indauth.service.demo;

import io.mosip.authentication.core.util.MatcherUtil;

/**
 * @author Sanjay Murali
 *
 */
public enum EmailMatchingStrategy implements MatchingStrategy {
	
	EXACT(MatchingStrategyType.EXACT, (reqInfo, entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			return MatcherUtil.doExactMatch((String) reqInfo, (String) entityInfo);
		} else {
			return 0;
		}
	});
	
	private final MatchFunction matchFunction;

	private final MatchingStrategyType matchStrategyType;

	/**
	 * 
	 * @param matchStrategyType
	 * @param matchValue
	 * @param matchFunction
	 */
	private EmailMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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
