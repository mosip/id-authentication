package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Map;
import java.util.function.ToIntBiFunction;
import io.mosip.authentication.core.util.DemoMatcherUtil;

/**
 * @author Manoj SP
 *
 */
public enum DOBTypeMatchingStrategy implements MatchingStrategy {

	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String) {
			return DemoMatcherUtil.doExactMatch((String) reqInfo, (String) entityInfo);
		} else {
			return 0;
		}
	});

	private final MatchingStrategyType matchStrategyType;

	private final MatchFunction matchFunction;

	private DOBTypeMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchStrategyType = matchStrategyType;
		this.matchFunction = matchFunction;
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
