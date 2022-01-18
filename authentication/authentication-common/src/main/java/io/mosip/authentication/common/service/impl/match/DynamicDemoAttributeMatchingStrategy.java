package io.mosip.authentication.common.service.impl.match;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.ID_NAME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MAPPING_CONFIG;

import java.util.Map;

import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;
import io.mosip.authentication.core.util.DemoMatcherUtil;

/**
 * The Enum DynamicDemoAttributeMatchingStrategy - used to compare and
 * evaluate the Dynamic Demographic attributes value received from the request and entity
 *
 * @author Loganathan Sekar
 * @author Nagarjuna
 */
public enum DynamicDemoAttributeMatchingStrategy implements TextMatchingStrategy {

	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			DemoMatcherUtil demoMatcherUtilObject = getDemoMatcherUtilObject(props);
			Object idNameObj = props.get(ID_NAME);
			Object mappingConfigObj = props.get(MAPPING_CONFIG);
			if(idNameObj instanceof String && mappingConfigObj instanceof MappingConfig) {
				MappingConfig mappingConfig = (MappingConfig) mappingConfigObj;
				String idName = (String) idNameObj;
				if(isNameAttribute(idName, mappingConfig)) {
					return TextMatchingStrategy.normalizeAndMatch(reqInfo, 
							entityInfo, 
							props,
							NameMatchingStrategy::normalizeText,
							demoMatcherUtilObject::doExactMatch);
				} else if(isFullAddressAttribute(idName, mappingConfig)) {
					return TextMatchingStrategy.normalizeAndMatch(reqInfo, 
							entityInfo, 
							props,
							FullAddressMatchingStrategy::normalizeText,
							demoMatcherUtilObject::doExactMatch);
				}
			}
			
			return demoMatcherUtilObject.doExactMatch((String)reqInfo, (String)entityInfo);
		} else if (reqInfo.equals(entityInfo)) {
			return DemoMatcherUtil.EXACT_MATCH_VALUE;
		}
		
		return 0;
	});

	/** The match function. */
	private final MatchFunction matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/**
	 * Instantiates a new email matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction the match function
	 */
	private DynamicDemoAttributeMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	private static boolean isFullAddressAttribute(String idName, MappingConfig mappingConfig) {
		return IdaIdMapping.FULLADDRESS
				.getMappingFunction()
				.apply(mappingConfig, DemoMatchType.ADDR)
				.contains(idName);
	}

	private static boolean isNameAttribute(String idName, MappingConfig mappingConfig) {
		return IdaIdMapping.NAME
				.getMappingFunction()
				.apply(mappingConfig, DemoMatchType.NAME)
				.contains(idName);
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#getType()
	 */
	@Override
	public MatchingStrategyType getType() {
		return matchStrategyType;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#getMatchFunction()
	 */
	@Override
	public MatchFunction getMatchFunction() {
		return matchFunction;
	}

	/**
	 * Gets the demoMatcherUtil object
	 * @param props
	 * @return
	 */
	public static DemoMatcherUtil getDemoMatcherUtilObject(Map<String, Object> props) {
		return (DemoMatcherUtil)props.get("demoMatcherUtil");
	}
}
