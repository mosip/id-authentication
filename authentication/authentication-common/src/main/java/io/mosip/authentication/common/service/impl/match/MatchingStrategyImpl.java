package io.mosip.authentication.common.service.impl.match;

import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategy;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

/**
 * The Class MatchingStrategyImpl - used to Instantiate the matching
 * strategy for bio based authentication to get match type and
 * match function
 * 
 *  @author Sanjay Murali
 * 
 */
public class MatchingStrategyImpl implements MatchingStrategy {
	
	/** The match strategy type. */
	private MatchingStrategyType matchStrategyType;

	/** The match function. */
	private MatchFunction matchFunction;
	
	/**
	 * Instantiates a new matching strategy impl.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction the match function
	 */
	public MatchingStrategyImpl(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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
	 * @see io.mosip.authentication.core.spi.indauth.match.MatchingStrategy#getMatchingStrategy()
	 */
	@Override
	public MatchingStrategy getMatchingStrategy() {
		return this;
	}

}
