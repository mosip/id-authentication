/**
 * 
 */
package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.function.ToIntBiFunction;

import io.mosip.authentication.core.dto.indauth.IdentityValue;
import io.mosip.authentication.core.util.MatcherUtil;

/**
 * @author Sanjay Murali
 *
 */
public enum GenderMatchingStrategy implements MatchingStrategy {

	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, IdentityValue entityInfo) -> {
		if (reqInfo instanceof String) {
			return MatcherUtil.doExactMatch((String) reqInfo, entityInfo.getValue());
		} else {
			return 0;
		}
	});

	private final ToIntBiFunction<Object, IdentityValue> matchFunction;

	private final MatchingStrategyType matchStrategyType;

	/**
	 * 
	 * @param matchStrategyType
	 * @param matchValue
	 * @param matchFunction
	 */
	GenderMatchingStrategy(MatchingStrategyType matchStrategyType,
			ToIntBiFunction<Object, IdentityValue> matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	@Override
	public MatchingStrategyType getType() {
		return matchStrategyType;
	}

	@Override
	public ToIntBiFunction<Object, IdentityValue> getMatchFunction() {
		return matchFunction;
	}

}
