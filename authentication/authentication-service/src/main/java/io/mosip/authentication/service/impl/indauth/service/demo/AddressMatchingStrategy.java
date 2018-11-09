package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.function.ToIntBiFunction;

import io.mosip.authentication.core.dto.indauth.IdentityValue;
import io.mosip.authentication.core.util.MatcherUtil;

/**
 * The Enum AddressMatchingStrategy.
 *
 * @author Dinesh Karuppiah.T
 */
public enum AddressMatchingStrategy implements MatchingStrategy {

	/** The exact. */
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, IdentityValue entityInfo) -> {
		if (reqInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeAddress((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeAddress(entityInfo.getValue());
			return MatcherUtil.doExactMatch(refInfoName, entityInfoName);
		} else {
			return 0;
		}
	});

	/** The match function. */
	private final ToIntBiFunction<Object, IdentityValue> matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/**
	 * Constructor for Address Matching Strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction     the match function
	 */
	AddressMatchingStrategy(MatchingStrategyType matchStrategyType,
			ToIntBiFunction<Object, IdentityValue> matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
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
	public ToIntBiFunction<Object, IdentityValue> getMatchFunction() {
		return matchFunction;
	}
}
