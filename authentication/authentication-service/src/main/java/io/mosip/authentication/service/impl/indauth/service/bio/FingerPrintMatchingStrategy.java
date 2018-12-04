package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.Map;

import io.mosip.authentication.service.impl.indauth.service.demo.MatchFunction;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategy;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategyType;

/**
 * @author Dinesh Karuppiah.T
 *
 */
public enum FingerPrintMatchingStrategy implements MatchingStrategy {

	PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			return (int) BioMatcherUtil.doPartialMatch((String) reqInfo, (String) entityInfo, props);
		} else {
			return 0;
		}
	});

	private final MatchingStrategyType matchStrategyType;

	private final MatchFunction matchFunction;

	private FingerPrintMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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
