package io.mosip.authentication.common.service.impl.match;

import java.util.Map;

import io.mosip.authentication.core.dto.DemoMatcherUtil;
import io.mosip.authentication.core.spi.bioauth.util.DemoNormalizer;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;

/**
 * The Enum AddressMatchingStrategy.
 *
 * @author Dinesh Karuppiah.T
 */
public enum AddressMatchingStrategy implements TextMatchingStrategy {

	/** The exact. */
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			Object demoNormalizerObject=  props.get("demoNormalizer");
			Object langObject=props.get("langCode");
			if(demoNormalizerObject instanceof  DemoNormalizer && langObject instanceof String) {
				DemoNormalizer demoNormalizer=(DemoNormalizer)demoNormalizerObject;
			    String langCode=(String)langObject;
				String refInfoName = demoNormalizer.normalizeAddress((String) reqInfo,langCode);
				String entityInfoName = demoNormalizer.normalizeAddress((String) entityInfo,langCode);
				return DemoMatcherUtil.doExactMatch(refInfoName, entityInfoName);
			}
			else
				return 0;
			
		} else {
			return 0;
		}
	});

	/** The match function. */
	private final MatchFunction matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/**
	 * Constructor for Address Matching Strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction     the match function
	 */
	AddressMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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
	public MatchFunction getMatchFunction() {
		return matchFunction;
	}
}
