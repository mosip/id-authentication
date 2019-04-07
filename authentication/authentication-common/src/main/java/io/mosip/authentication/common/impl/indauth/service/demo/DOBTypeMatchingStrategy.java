package io.mosip.authentication.common.impl.indauth.service.demo;

import java.util.Map;

import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;
import io.mosip.authentication.core.util.DemoMatcherUtil;

/**
 * @author Manoj SP
 *
 */
public enum DOBTypeMatchingStrategy implements TextMatchingStrategy {

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
