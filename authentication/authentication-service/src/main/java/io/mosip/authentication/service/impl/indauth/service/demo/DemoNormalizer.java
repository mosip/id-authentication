package io.mosip.authentication.service.impl.indauth.service.demo;

/**
 * Generic class to normalize individual name, address.
 *
 * @author Rakesh Roshan
 */
public final class DemoNormalizer {


	private static final String REGEX_SPECIAL_CHARACTERS = "[\\.|,|\\-|\\*|\\(|\\)|\\[|\\]|`|\\'|/|\\|#|\"]";
	private static final String REGEX_SALUTATION = "(M|m|D|d)(rs?)(.)";
	private static final String REGEX_CARE_OF_LABLE = "(C|c|S|s|D|d|W|w|H|h)/[O|o]";
	private static final String OTHER = "(N|n)(O|o)(\\.)?";
	private static final String REGEX_WHITE_SPACE = "\\s+";

	private DemoNormalizer() {

	}

	public static String normalizeName(String name) {

		name = name.replaceAll(REGEX_SALUTATION, "").replaceAll(REGEX_SPECIAL_CHARACTERS, " ")
				.replaceAll(REGEX_WHITE_SPACE, " ").trim();

		return name;
	}

	public static String normalizeAddress(String address) {

		address = address.replace(REGEX_CARE_OF_LABLE, "").replaceAll(REGEX_SPECIAL_CHARACTERS, " ")
				.replaceAll(OTHER, "").replaceAll(REGEX_WHITE_SPACE, " ").trim();

		return address;
	}

}
