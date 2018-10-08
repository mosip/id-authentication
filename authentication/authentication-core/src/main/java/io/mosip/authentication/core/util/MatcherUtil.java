package io.mosip.authentication.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MatcherUtil {

	private MatcherUtil() {

	}

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

	public static int doLessThanEqualToMatch(int reqInfo, int entityInfo) {
		return reqInfo <= entityInfo ? 100 : 0;
	}

	public static int doExactMatch(Date reqInfo, Date entityInfo) {
		return reqInfo.compareTo(entityInfo) == 0 ? 100 : 0;
	}

	private static List<String> split(String str) {
		return Stream.of(str.toLowerCase().split("\\s+")).filter(s -> s.length() > 0).collect(Collectors.toList());
	}

	private static List<String> fetchNotMatchedList(List<String> refInfoList, List<String> entityInfoList) {
		return refInfoList.parallelStream().filter(value -> !entityInfoList.contains(value))
				.collect(Collectors.toList());
	}

	private static boolean allMatch(List<String> refInfoList, List<String> entityInfoList) {
		return entityInfoList.parallelStream().allMatch(name -> refInfoList.contains(name));
	}

}
