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

import io.mosip.kernel.core.util.StringUtils;

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

    /**
     * Gets the threshold from property file.
     * 
     * TODO need to check whether to return null or error if threshold validation
     * fails.
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
     * TODO need to use {@link FileUtil}
     *
     * @param property
     *            the property
     * @return the property
     */
    private static final String getProperty(String property) {
	InputStream propertiesFileStream = null;

	Properties textMatcherProp = new Properties();

	try {
	    propertiesFileStream = TextMatcherUtil.class.getClass().getResourceAsStream(MOSIP_PROPERTIES_FILE);
	    textMatcherProp.load(propertiesFileStream);
	} catch (IOException e) {
	    System.err.println(e.getMessage());
	}

	return textMatcherProp.getProperty(property);
    }

    /**
     * Validates whether threshold is between 0 and 100 and is not null.
     *
     * TODO need to check whether to return false or error if threshold validation
     * fails.
     * 
     * @param threshold
     *            the threshold
     * @return true, if successful
     */
    private static final boolean validateThreshold(Integer threshold) {
	if (threshold >= 0 && threshold <= 100 && threshold != null) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Checks whether two strings are exactly matching or not.
     *
     * @param inputString
     *            the input string
     * @param storedString
     *            the stored string
     * @return true, if successful, else false
     */
    public static final boolean exactMatch(String inputString, String storedString) {
	if (StringUtils.equalsIgnoreCase(inputString, storedString)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Checks whether two strings are partially matching or not.
     *
     * @param inputString
     *            the input string
     * @param storedString
     *            the stored string
     * @return true, if successful
     */
    public static final boolean partialMatch(String inputString, String storedString) {
	if (StringUtils.containsIgnoreCase(inputString, storedString)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * This method gets two strings as input along with their language and gets
     * their phonetic values using PhoneticEngine class. Then the phonetic matching
     * probability is calculated using Soundex class and validated against user
     * threshold or default configured threshold. If the matching probability of
     * both phonetic strings are greater than or equal to the given threshold, then
     * the result is true, else false.
     *
     * TODO 1. need to validate the language given is supported or not. 2. need to
     * log exceptions using Logger
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
    public static final boolean phoneticMatch(String inputString, String storedString, Integer threshold,
	    String language) {
	if (threshold == null) {
	    threshold = getThreshold();
	}
	if (validateThreshold(threshold)) {
	    try {
		Integer thresholdProbability = phoneticsMatch(inputString, storedString, language);

		if (validateThreshold(thresholdProbability) && thresholdProbability >= threshold) {
		    return true;
		} else {
		    return false;
		}
	    } catch (EncoderException e) {
		System.err.println(e.getMessage());
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
