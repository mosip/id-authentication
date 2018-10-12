package io.mosip.authentication.service.impl.indauth.service.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.mosip.authentication.core.util.MatcherUtil;

/**
 * @author Sanjay Murali
 *
 */
public enum DOBMatchingStrategy implements MatchingStrategy {
	
	EXACT(MatchStrategyType.EXACT, (reqInfo, entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date reqInfoDate = null;
			Date entityInfoDate = null;
			try {
				reqInfoDate = sdf.parse((String) reqInfo);
				entityInfoDate = sdf.parse((String) entityInfo);
				return MatcherUtil.doExactMatch(reqInfoDate,entityInfoDate);
			} catch (ParseException e) {
				//Fix Me
				return 0;
			}
		} 
		return 0;
	});
	
	
	private final MatchFunction matchFunction;

	private final MatchStrategyType matchStrategyType;

	/**
	 * 
	 * @param matchStrategyType
	 * @param matchValue
	 * @param matchFunction
	 */
	private DOBMatchingStrategy(MatchStrategyType matchStrategyType, MatchFunction matchFunction) {
		this.matchFunction = matchFunction;
		this.matchStrategyType = matchStrategyType;
	}

	@Override
	public MatchStrategyType getType() {
		return matchStrategyType;
	}

	@Override
	public MatchFunction getMatchFunction() {
		return matchFunction;
	}

}
