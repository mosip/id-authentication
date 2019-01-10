package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.Map;
import java.util.function.BiFunction;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.irisauth.provider.IrisProvider;

/**
 * The Enum CompositeIrisMatchingStrategy.
 * 
 * @author Sanjay Murali
 */
public enum CompositeIrisMatchingStrategy implements MatchingStrategy {

	PARTIAL(MatchingStrategyType.PARTIAL, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof Map && entityInfo instanceof Map) {
			Object object = props.get(IrisProvider.class.getSimpleName()); //TODO add iris provider
			if (object instanceof BiFunction) {
				BiFunction<Map<String, String>, Map<String, Object>, Double> func = (BiFunction<Map<String, String>, Map<String,Object>, Double>) object;
				return (int) func.apply((Map<String, String>) reqInfo, props).doubleValue();
			}else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNKNOWN_ERROR);
			}
		}
		return 0;
	});

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/** The match function. */
	private final MatchFunction matchFunction;

	/**
	 * Instantiates a new composite iris matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction the match function
	 */
	private CompositeIrisMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#match(java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	public int match(Map<String, String> reqValues, Map<String, String> entityValues,
			Map<String, Object> matchProperties) throws IdAuthenticationBusinessException {
		return matchFunction.match(reqValues, entityValues, matchProperties);
	}

}
