package io.mosip.authentication.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.commons.codec.language.bm.NameType;
import org.apache.commons.codec.language.bm.PhoneticEngine;
import org.apache.commons.codec.language.bm.RuleType;

import ch.qos.logback.core.util.FileUtil;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class TextMatcherUtil is used to match two input strings and to provide
 * results based on the method called.
 * 
 * @author Manoj SP
 */
public final class TextMatcherUtil {

	/** The Constant MOSIP_PROPERTIES_FILE. */
	private static final String MOSIP_PROPERTIES_FILE = "/mosip.properties";

	/** The Constant THRESHOLD_PROPERTY. */
	private static final String THRESHOLD_PROPERTY = "mosip.textmatcher.threshold";

	private static Logger mosipLogger = IdaLogger.getLogger(TextMatcherUtil.class);

	private static final String SESSION_ID = "sessionId";

	private TextMatcherUtil() {
	}

	/**
	 * Gets the threshold from property file.
	 *
	 * @return the threshold
	 */
	private static final Integer getThreshold() {
		Integer threshold = null;
		threshold = Integer.parseInt(getProperty(THRESHOLD_PROPERTY));
		if (validateThreshold(threshold)) {
			return threshold;
		} else {
			return null;
		}
	}

	/**
	 * Gets the properties from property file.
	 *
	 * @param property the property
	 * @return the property
	 */
	private static final String getProperty(String property) {
		InputStream propertiesFileStream = null;

		Properties textMatcherProp = new Properties();

		try {
			propertiesFileStream = TextMatcherUtil.class.getClass().getResourceAsStream(MOSIP_PROPERTIES_FILE);
			textMatcherProp.load(propertiesFileStream);
		} catch (IOException e) {
			mosipLogger.error(SESSION_ID, "file not found", "IOException", e.getMessage());
		}

		return textMatcherProp.getProperty(property);
	}

	/**
	 * Validates whether threshold is between 0 and 100 and is not null.
	 * 
	 * @param threshold the threshold
	 * @return true, if successful
	 */
	private static final boolean validateThreshold(Integer threshold) {
		return threshold >= 0 && threshold <= 100;
	}

	/**
	 * This method gets two strings as input along with their language and gets
	 * their phonetic values using PhoneticEngine class. Then the phonetic matching
	 * probability is calculated using Soundex class and validated against user
	 * threshold or default configured threshold. If the matching probability of
	 * both phonetic strings are greater than or equal to the given threshold, then
	 * the result is true, else false.
	 *
	 * 
	 * @param inputString  the input string
	 * @param storedString the stored string
	 * @param threshold    the threshold
	 * @param language     the language
	 * @return true, if successful
	 */
	public static final boolean phoneticMatch(String inputString, String storedString, Integer threshold,
			String language) {
		if (threshold == null) {
			threshold = getThreshold();
		}
		if (validateThreshold(threshold)) {
			try {
				Integer thresholdProbability = phoneticsMatch(inputString, storedString, language);

				return (validateThreshold(thresholdProbability) && (thresholdProbability >= threshold));
			} catch (EncoderException e) {
				mosipLogger.error(SESSION_ID, "Encoding", "EncoderException", e.getMessage());
			}
		}
		return false;
	}

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
