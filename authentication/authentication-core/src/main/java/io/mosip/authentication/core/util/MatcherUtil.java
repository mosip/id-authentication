package io.mosip.authentication.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * @author Dinesh Karuppiah
 */

public final class MatcherUtil {

	private MatcherUtil() {

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

		if (refInfoList.size() == entityInfoList.size() && allMatch(refInfoList, entityInfoList)
				&& fetchNotMatchedList(refInfoList, entityInfoList).isEmpty()) {
			matchvalue = 100;
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

		refInfoList.forEach(str -> {
			if (entityInfoList.contains(str)) {
				matchedList.add(str);
				entityInfoList.remove(str);
			} else {
				unmatchedList.add(str);
			}
		});

		new ArrayList<>(unmatchedList).stream().filter(str -> str.length() == 1).forEach(s -> {
			Optional<String> matchingWord = entityInfoList.stream().filter(str -> str.startsWith(s)).findAny();
			if (matchingWord.isPresent()) {
				entityInfoList.remove(matchingWord.get());
				unmatchedList.remove(s);
			}
		});

		matchvalue = matchedList.size() * 100 / (originalEntityInfoList.size() + unmatchedList.size());

		return matchvalue;
	}

	public static int doPhoneticsMatch(String reqInfo, String entityInfo) {
		// TODO yet to be coded
		return 0;
	}

	/**
	 * Do Less than or equal to match based on input integer value
	 * 
	 * @param reqInfo
	 * @param entityInfo
	 * @return
	 */
	public static int doLessThanEqualToMatch(int reqInfo, int entityInfo) {
		return reqInfo <= entityInfo ? 100 : 0;
	}

	/**
	 * Exact match for Date - checks refInfo date and entity info date are same
	 * 
	 * @param reqInfo
	 * @param entityInfo
	 * @return 100 when the refInfo and entityInfo dates are matched
	 */
	public static int doExactMatch(Date reqInfo, Date entityInfo) {
		return reqInfo.compareTo(entityInfo) == 0 ? 100 : 0;
	}

	/**
	 * Split the string based on empty String and convert to words
	 * 
	 * @param str
	 * @return
	 */

	private static List<String> split(String str) {
		return Stream.of(str.toLowerCase().split("\\s+")).filter(s -> s.length() > 0).collect(Collectors.toList());
	}

	/**
	 * filters the refInfoList and compares with entityInfo List and return Not
	 * matched values as List
	 * 
	 * @param refInfoList
	 * @param entityInfoList
	 * @return
	 */

	private static List<String> fetchNotMatchedList(List<String> refInfoList, List<String> entityInfoList) {
		return refInfoList.parallelStream().filter(value -> !entityInfoList.contains(value))
				.collect(Collectors.toList());
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
		return entityInfoList.parallelStream().allMatch(name -> refInfoList.contains(name));
	}

}
