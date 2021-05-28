package io.mosip.authentication.common.service.impl.match;

import java.util.Map;

import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;
import io.mosip.authentication.core.util.DemoMatcherUtil;

/**
 * The Enum DOBTypeMatchingStrategy.
 * 
 * @author Manoj SP
 * @author Nagarjuna
 */
public enum DOBTypeMatchingStrategy implements TextMatchingStrategy {

	/** The exact. */
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String) {
			return getDemoMatcherUtilObject(props).doExactMatch((String) reqInfo, (String) entityInfo);
		} else {
			return 0;
		}
	});

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/** The match function. */
	private final MatchFunction matchFunction;

	/**
	 * Instantiates a new DOB type matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction the match function
	 */
	private DOBTypeMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchStrategyType = matchStrategyType;
		this.matchFunction = matchFunction;
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
