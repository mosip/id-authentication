package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.function.ToIntBiFunction;

import io.mosip.authentication.core.util.MatcherUtil;

/**
 * @author Sanjay Murali
 *
 */
public enum PhoneNoMatchingStrategy implements MatchingStrategy {
	
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			return MatcherUtil.doExactMatch((String) reqInfo, (String) entityInfo);
		} else {
			return 0;
		}
	});
	
	private final ToIntBiFunction<Object, Object> matchFunction;

	private final MatchingStrategyType matchStrategyType;

	/**
	 * 
	 * @param matchStrategyType
	 * @param matchValue
	 * @param matchFunction
	 */
	private PhoneNoMatchingStrategy(MatchingStrategyType matchStrategyType, ToIntBiFunction<Object, Object> matchFunction) {
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
