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
	
	EXACT(MatchingStrategyType.EXACT, (reqInfo, entityInfo) -> {
		if (reqInfo instanceof String && entityInfo instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date entityInfoDate = (Date)entityInfo;
				Date reqInfoDate = sdf.parse((String) reqInfo);
				return MatcherUtil.doExactMatch(reqInfoDate,entityInfoDate);
			} catch (ParseException e) {
				//FIXME
				return 0;
			}
		} 
		return 0;
	});
	
	
	private final MatchFunction matchFunction;

	private final MatchingStrategyType matchStrategyType;

	/**
	 * 
	 * @param matchStrategyType
	 * @param matchValue
	 * @param matchFunction
	 */
	private DOBMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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
