package io.mosip.authentication.service.impl.indauth.service.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchFunction;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TextMatchingStrategy;
import io.mosip.authentication.core.util.DemoMatcherUtil;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Enum DOBMatchingStrategy.
 *
 * @author Sanjay Murali
 */
public enum DOBMatchingStrategy implements TextMatchingStrategy {

	/** The exact. */
	EXACT(MatchingStrategyType.EXACT, (Object reqInfo, Object entityInfo, Map<String, Object> props) -> {
		if (reqInfo instanceof String && entityInfo instanceof String) {
			try {
//				String dateFormatReq = ((Environment)props.get(Environment.class.getSimpleName())).getProperty("date.pattern");
				//FIXME get from property
				String dateFormatReq = "dd/MM/yyyy";

				String dateFormatEntity = "yyyy/MM/dd";
				Date reqInfoDate = DateUtils.parseToDate((String) reqInfo, dateFormatReq);
				Date entityInfoDate = DateUtils.parseToDate((String) entityInfo, dateFormatEntity);
				return DemoMatcherUtil.doExactMatch(reqInfoDate, entityInfoDate);
			} catch (ParseException e) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DOB_MISMATCH, e);
				/** The match function. */
				// FIXME
//				return 0;
			}
		}
		return 0;
	});

	private final MatchFunction matchFunction;

	/** The match strategy type. */
	private final MatchingStrategyType matchStrategyType;

	/**
	 * Instantiates a new DOB matching strategy.
	 *
	 * @param matchStrategyType the match strategy type
	 * @param matchFunction     the match function
	 */
	DOBMatchingStrategy(MatchingStrategyType matchStrategyType, MatchFunction matchFunction) {
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
