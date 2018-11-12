package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.function.ToIntBiFunction;

import io.mosip.authentication.core.dto.indauth.IdentityValue;
import io.mosip.authentication.core.util.MatcherUtil;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public enum NameMatchingStrategy implements MatchingStrategy {

	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, IdentityValue entityInfo) -> {
		if (reqInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeName((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeName(entityInfo.getValue());
			return MatcherUtil.doExactMatch(refInfoName, entityInfoName);
		} else {
			return 0;
		}
	}), PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, IdentityValue entityInfo) -> {
		if (reqInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeName((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeName(entityInfo.getValue());
			return MatcherUtil.doPartialMatch(refInfoName, entityInfoName);
		} else {
			return 0;
		}
	}), PHONETICS(MatchingStrategyType.PHONETICS, (Object reqInfo, IdentityValue entityInfo) -> {
		if (reqInfo instanceof String) {
			String refInfoName = DemoNormalizer.normalizeName((String) reqInfo);
			String entityInfoName = DemoNormalizer.normalizeName(entityInfo.getValue());
			return MatcherUtil.doPhoneticsMatch(refInfoName, entityInfoName,entityInfo.getLanguage());
		} else {
			return 0;
		}
	});

	private final ToIntBiFunction<Object, IdentityValue> matchFunction;

	private final MatchingStrategyType matchStrategyType;

	/**
	 * Constructor for Name Matching Strategy
	 * 
	 * @param matchStrategyType
	 * @param matchFunction
	 */
	NameMatchingStrategy(MatchingStrategyType matchStrategyType, ToIntBiFunction<Object, IdentityValue> matchFunction) {
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
