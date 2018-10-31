package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.function.ToIntBiFunction;

import io.mosip.authentication.core.dto.indauth.IdentityValue;
import io.mosip.authentication.core.util.MatcherUtil;

/**
 * @author Manoj SP
 *
 */
public enum DOBTypeMatchingStrategy implements MatchingStrategy {

	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, IdentityValue entityInfo) -> {
		if (reqInfo instanceof String) {
			return MatcherUtil.doExactMatch((String) reqInfo, entityInfo.getValue());
		} else {
			return 0;
		}
	});

	private final MatchingStrategyType matchStrategyType;

	private final ToIntBiFunction<Object, IdentityValue> matchFunction;

	private DOBTypeMatchingStrategy(MatchingStrategyType matchStrategyType,
			ToIntBiFunction<Object, IdentityValue> matchFunction) {
		this.matchStrategyType = matchStrategyType;
		this.matchFunction = matchFunction;
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
