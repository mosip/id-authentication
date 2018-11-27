package io.mosip.authentication.core.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.commons.codec.language.bm.NameType;
import org.apache.commons.codec.language.bm.PhoneticEngine;
import org.apache.commons.codec.language.bm.RuleType;

/**
 * The Class TextMatcherUtil is used to match two input strings and to provide
 * results based on the method called.
 * 
 * @author Manoj SP
 */
public final class TextMatcherUtil {

	/**
	 * This method gets two strings as input along with their language and gets
	 * their phonetic values using PhoneticEngine class. Then the phonetic matching
	 * probability is calculated using Soundex class and validated against user
	 * threshold or default configured threshold. If the matching probability of
	 * both phonetic strings are greater than or equal to the given threshold, then
	 * the result is true, else false.
	 *
	 * 
	 * @param inputString
	 *            the input string
	 * @param storedString
	 *            the stored string
	 * @param threshold
	 *            the threshold
	 * @param language
	 *            the language
	 * @return true, if successful
	 */
	public static Integer phoneticsMatch(String inputString, String storedString, String language)
			throws EncoderException {
		PhoneticEngine phoneticEngine = new PhoneticEngine(NameType.GENERIC, RuleType.EXACT, true);

		Soundex soundex = new Soundex();

		Set<String> languageSet = new HashSet<>();
		languageSet.add(language);

		String encodedInputString = phoneticEngine.encode(inputString, Languages.LanguageSet.from(languageSet));

		String encodedStoredString = phoneticEngine.encode(storedString, Languages.LanguageSet.from(languageSet));

		return (soundex.difference(encodedInputString, encodedStoredString) + 1) * 20;
	}

}
