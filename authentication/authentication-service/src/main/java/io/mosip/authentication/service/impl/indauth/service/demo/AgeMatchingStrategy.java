package io.mosip.authentication.service.impl.indauth.service.demo;

import io.mosip.authentication.core.util.MatcherUtil;

/**
 * @author Sanjay Murali
 *
 */
public enum AgeMatchingStrategy implements MatchingStrategy {
	
	EXACT(MatchingStrategyType.EXACT, (reqInfo, entityInfo) -> {
		if (reqInfo instanceof Integer && entityInfo instanceof Integer) {
			return MatcherUtil.doLessThanEqualToMatch((int) reqInfo, (int) entityInfo);
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
	private AgeMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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
