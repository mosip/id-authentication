package io.mosip.authentication.service.impl.indauth.service.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.ToIntBiFunction;

import io.mosip.authentication.core.util.MatcherUtil;

/**
 * The Enum DOBMatchingStrategy.
 *
 * @author Sanjay Murali
 */
public enum DOBMatchingStrategy implements MatchingStrategy {
	
	/** The exact. */
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date entityInfoDate = (Date)entityInfo;
				Date reqInfoDate = sdf.parse((String) reqInfo);
				return MatcherUtil.doExactMatch(reqInfoDate,entityInfoDate);
			} catch (ParseException e) {
				/** The match function. */
				//FIXME
				return 0;
			}
		} 
		return 0;
	});
	
	
	private final ToIntBiFunction<Object, Object> matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/**
	 * Instantiates a new DOB matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction the match function
	 */
	DOBMatchingStrategy(MatchingStrategyType matchStrategyType,  ToIntBiFunction<Object, Object> matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategy#getType()
	 */
	@Override
	public MatchingStrategyType getType() {
		return matchStrategyType;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategy#getMatchFunction()
	 */
	@Override
	public ToIntBiFunction<Object, Object> getMatchFunction() {
		return matchFunction;
	}

}
