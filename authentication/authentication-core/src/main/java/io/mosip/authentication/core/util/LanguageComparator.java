package io.mosip.authentication.core.util;

import java.util.Comparator;
import java.util.List;

/**
 * This is the class to sort languages
 * @author Nagarjuna
 *
 */
public class LanguageComparator implements Comparator<String> {

	List<String> sortOnLanguageCodes;
	
	public LanguageComparator(List<String> sortOnLanguageCodes) {
		this.sortOnLanguageCodes = sortOnLanguageCodes;
	}
	
	
	@Override
	public int compare(String langCode1, String langCode2) {
		int indexInSortOnLangCode1 = sortOnLanguageCodes.indexOf(langCode1);
		int indexInSortOnLangCode2 = sortOnLanguageCodes.indexOf(langCode2);

		if (indexInSortOnLangCode1 < 0) {
			indexInSortOnLangCode1 = Integer.MAX_VALUE;
		}
		if (indexInSortOnLangCode2 < 0) {
			indexInSortOnLangCode2 = Integer.MAX_VALUE;
		}
		return Integer.compare(indexInSortOnLangCode1, indexInSortOnLangCode2);
	}
}
