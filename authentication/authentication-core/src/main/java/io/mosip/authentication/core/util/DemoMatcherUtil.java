package io.mosip.authentication.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.EncoderException;

import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * 
 * @author Dinesh Karuppiah
 */

public final class DemoMatcherUtil {

	private static final String SPLIT_REGEX = "\\s+";
	private static final Integer EXACT_MATCH_VALUE = 100;

	private static Logger mosipLogger = IdaLogger.getLogger(DemoMatcherUtil.class);
	private static final String SESSION_ID = "sessionId";

	private DemoMatcherUtil() {

	}

	/**
	 * Do Exact match on Request Info details and Entity Info details string
	 * 
	 * @param reqInfo
	 * @param entityInfo
	 * @return 0 or 100 based on match value
	 */
	public static int doExactMatch(String reqInfo, String entityInfo) {
		int matchvalue = 0;
		List<String> refInfoList = split(reqInfo);
		List<String> entityInfoList = split(entityInfo);

		if (refInfoList.size() == entityInfoList.size() && allMatch(refInfoList, entityInfoList)) {
			matchvalue = EXACT_MATCH_VALUE;
		}
		return matchvalue;
	}

	/**
	 * Do Partial Match for Reference info details and Entity Info details string
	 * 
	 * @param reqInfo
	 * @param entityInfo
	 * @return
	 */
	public static int doPartialMatch(String reqInfo, String entityInfo) {
		int matchvalue = 0;
		List<String> refInfoList = split(reqInfo);
		List<String> originalEntityInfoList = split(entityInfo);
		List<String> entityInfoList = Collections.synchronizedList(new ArrayList<>(originalEntityInfoList));
		List<String> matchedList = new ArrayList<>();
		List<String> unmatchedList = new ArrayList<>();
		refInfoList.forEach((String refInfo) -> {
			if (entityInfoList.contains(refInfo)) {
				matchedList.add(refInfo);
				entityInfoList.remove(refInfo);
			} else {
				unmatchedList.add(refInfo);
			}
		});
		new ArrayList<>(unmatchedList).stream().filter(str -> str.length() == 1).forEach((String s) -> {
			Optional<String> matchingWord = entityInfoList.stream().filter(str -> str.startsWith(s)).findAny();
			if (matchingWord.isPresent()) {
				entityInfoList.remove(matchingWord.get());
				unmatchedList.remove(s);
			}
		});
		matchvalue = matchedList.size() * EXACT_MATCH_VALUE / (originalEntityInfoList.size() + unmatchedList.size());
		return matchvalue;
	}

	/**
	 * Do Less than or equal to match based on input integer value
	 * 
	 * @param reqInfo
	 * @param entityInfo
	 * @return
	 */
	public static int doLessThanEqualToMatch(int reqInfo, int entityInfo) {
		if (reqInfo <= entityInfo) {
			return EXACT_MATCH_VALUE;
		} else {
			return 0;
		}
	}

	/**
	 * Exact match for Date - checks refInfo date and entity info date are same
	 * 
	 * @param reqInfo
	 * @param entityInfo
	 * @return 100 when the refInfo and entityInfo dates are matched
	 */
	public static int doExactMatch(Date reqInfo, Date entityInfo) {
		if(DateUtils.isSameInstant(reqInfo, entityInfo)) {
			return EXACT_MATCH_VALUE;
		} else {
			return 0;
		}
	}

	/**
	 * Split the string based on empty String and convert to words
	 * 
	 * @param str
	 * @return
	 */

	private static List<String> split(String str) {
		return Stream.of(str.toLowerCase().split(SPLIT_REGEX)).filter(s -> s.length() > 0).collect(Collectors.toList());
	}

	/**
	 * returns boolean values based on all match value on entityInfo List and
	 * refInfoList values
	 * 
	 * @param refInfoList
	 * @param entityInfoList
	 * @return
	 */

	private static boolean allMatch(List<String> refInfoList, List<String> entityInfoList) {
		return entityInfoList.parallelStream().allMatch(str -> refInfoList.contains(str));
	}

	/**
	 * 
	 * @param refInfoName @param entityInfoName @return @throws
	 */
	public static int doPhoneticsMatch(String refInfoName, String entityInfoName, String language) {
		int value = 0;
		try {
			value = TextMatcherUtil.phoneticsMatch(refInfoName, entityInfoName, language);
		} catch (EncoderException e) {
			mosipLogger.error(SESSION_ID, "doPhoneticsMatch", "EncoderException", e.getMessage());
		}

		return value;
	}

}
