package io.mosip.authentication.core.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.demographics.spi.IDemoApi;

/**
 * 
 * Util class for Demo Matcher
 * 
 * @author Dinesh Karuppiah
 * @author Nagarjuna
 */

@Component
public class DemoMatcherUtil {
	
	public static final int EXACT_MATCH_VALUE = 100;

	//private Logger mosipLogger = IdaLogger.getLogger(DemoMatcherUtil.class);

	@Autowired
	private IDemoApi iDemoApi;
	
	/**
	 * Instantiates a new demo matcher util.
	 */
	private DemoMatcherUtil() {

	}

	/**
	 * Do Exact match on Request Info details and Entity Info details string.
	 *
	 * @param reqInfo    the req info
	 * @param entityInfo the entity info
	 * @return 0 or 100 based on match value
	 */
	public int doExactMatch(String reqInfo, String entityInfo) {
		return iDemoApi.doExactMatch(reqInfo, entityInfo, null);
	}

	/**
	 * Do Partial Match for Reference info details and Entity Info details string.
	 *
	 * @param reqInfo    the req info
	 * @param entityInfo the entity info
	 * @return the int
	 */
	public int doPartialMatch(String reqInfo, String entityInfo) {
		return iDemoApi.doPartialMatch(reqInfo, entityInfo, null);
	}

	/**
	 * Do Less than or equal to match based on input integer value.
	 *
	 * @param reqInfo    the req info
	 * @param entityInfo the entity info
	 * @return the int
	 */
	public int doLessThanEqualToMatch(int reqInfo, int entityInfo) {
		if (reqInfo <= entityInfo) {
			return EXACT_MATCH_VALUE;
		} else {
			return 0;
		}
	}

	/**
	 * Exact match for Date - checks refInfo date and entity info date are same.
	 *
	 * @param reqInfo    the req info
	 * @param entityInfo the entity info
	 * @return 100 when the refInfo and entityInfo dates are matched
	 */
	public int doExactMatch(Date reqInfo, Date entityInfo) {
		if (DateUtils.isSameInstant(reqInfo, entityInfo)) {
			return EXACT_MATCH_VALUE;
		} else {
			return 0;
		}
	}


	/**
	 * Doing phonetic match with input request and stored-request with
	 * language-name,NOT language-code. If give language code, get
	 * java.lang.IllegalArgumentException: No rules found for gen, rules,
	 * language-code.
	 *
	 * @param refInfoName    the ref info list
	 * @param entityInfoName the entity info name
	 * @param language       the language
	 * @return the int
	 */
	public int doPhoneticsMatch(String refInfoName, String entityInfoName, String language) {
		return iDemoApi.doPhoneticsMatch(refInfoName, entityInfoName, language, null);
	}
}
