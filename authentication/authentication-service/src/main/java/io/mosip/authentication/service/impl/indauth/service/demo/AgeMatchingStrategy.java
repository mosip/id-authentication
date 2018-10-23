package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.function.ToIntBiFunction;

import io.mosip.authentication.core.util.MatcherUtil;

/**
 * The Enum AgeMatchingStrategy.
 *
 * @author Sanjay Murali
 */
public enum AgeMatchingStrategy implements MatchingStrategy {
	
	/** The exact. */
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo) -> {
		if (reqInfo instanceof Integer && entityInfo instanceof Integer) {
			return MatcherUtil.doLessThanEqualToMatch((int) reqInfo, (int) entityInfo);
		} else {
			return 0;
		}
	});
	
	/** The match function. */
	private final ToIntBiFunction<Object, Object> matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/**
	 * Instantiates a new age matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction the match function
	 */
	private AgeMatchingStrategy(MatchingStrategyType matchStrategyType, ToIntBiFunction<Object, Object> matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategy#getType()
	 */
	@Override
	public MatchingStrategyType getType() {
		return matchStrategyType;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategy#getMatchFunction()
	 */
	@Override
	public ToIntBiFunction<Object, Object> getMatchFunction() {
		return matchFunction;
	}

}
