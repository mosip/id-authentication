/**
 * 
 */
package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Date;

import io.mosip.authentication.core.util.MatcherUtil;

/**
 * @author M1047395
 *
 */
public enum DOBMatchingStrategy implements MatchingStrategy {
	
	EXACT(MatchStrategyType.EXACT, (reqInfo, entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			//TODO string to date conversion
			return MatcherUtil.doExactMatch((Date) reqInfo,(Date) entityInfo);
		} else {
			return 0;
		}
	});
	
	private final MatchFunction matchFunction;

	private final MatchStrategyType matchStrategyType;

	/**
	 * 
	 * @param matchStrategyType
	 * @param matchValue
	 * @param matchFunction
	 */
	private DOBMatchingStrategy(MatchStrategyType matchStrategyType, MatchFunction matchFunction) {
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
