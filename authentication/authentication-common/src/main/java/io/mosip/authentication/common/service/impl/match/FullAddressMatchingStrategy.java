package io.mosip.authentication.common.service.impl.match;

import java.util.Map;

import io.mosip.authentication.core.dto.DemoMatcherUtil;
import io.mosip.authentication.core.spi.bioauth.util.DemoNormalizer;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;

/**
 * 
 * Matching Strategy for Full Address entity
 * 
 * @author Dinesh Karuppiah.T
 */

public enum FullAddressMatchingStrategy implements TextMatchingStrategy {

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

	}), PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
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
	}), PHONETICS(MatchingStrategyType.PHONETICS, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			Object demoNormalizerObject=  props.get("demoNormalizer");
			Object langObject=props.get("langCode");
			if(demoNormalizerObject instanceof  DemoNormalizer && langObject instanceof String) {
				DemoNormalizer demoNormalizer=(DemoNormalizer)demoNormalizerObject;
			    String langCode=(String)langObject;
				String refInfoName = demoNormalizer.normalizeAddress((String) reqInfo,langCode);
				String entityInfoName = demoNormalizer.normalizeAddress((String) entityInfo,langCode);
				String language = (String) props.get("language");
				return DemoMatcherUtil.doPhoneticsMatch(refInfoName, entityInfoName, language);
			}
			else
				return 0;
		} else {
			return 0;
		}
	});
	private final MatchFunction matchFunction;

	private final MatchingStrategyType matchStrategyType;

	/**
	 * Constructor for Full Address Matching Strategy
	 * 
	 * @param matchStrategyType
	 * @param matchFunction
	 */
	FullAddressMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
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
