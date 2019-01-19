package io.mosip.authentication.service.impl.indauth.service.demo;

/**
 * Generic class to normalize individual name, address.
 *
 * @author Rakesh Roshan
 */
public final class DemoNormalizer {


	private static final String REGEX_SPECIAL_CHARACTERS = "[\\.|,|\\-|\\*|\\(|\\)|\\[|\\]|`|\\'|/|\\|#|\"]";
	private static final String REGEX_SALUTATION = "(M|m|D|d)(rs?)(.)";
	private static final String REGEX_CARE_OF_LABLE = "[CcSsDdWwHh]/[Oo]";
	private static final String OTHER = "(N|n)(O|o)(\\.)?";
	private static final String REGEX_WHITE_SPACE = "\\s+";
	private static final String APARTMENT = "[aA][pP][aA][rR][tT][mM][eE][nN][tT]";
	private static final String STREET = "[sS][tT][rR][eE][eE][tT]";
	private static final String ROAD = "[rR][oO][aA][dD]";
	private static final String MAIN = "[mM][aA][iI][nN]";
	private static final String CROSS = "[cC][rR][oO][sS][sS]";
	private static final String SECTOR = "[sS][eE][cC][tT][oO][rR]";
	private static final String OPPOSITE = "[oO][pP][pP][oO][sS][iI][tT][eE]";
	private static final String MARKET = "[mM][aA][rR][kK][eE][tT]";

	private DemoNormalizer() {

	}

	public static String normalizeName(String name) {

		name = name.replaceAll(REGEX_SALUTATION, "")
					.replaceAll(REGEX_SPECIAL_CHARACTERS, "")
					.replaceAll(REGEX_WHITE_SPACE, " ")
					.trim();

		return name;
	}

	public static String normalizeAddress(String address) {

		address = address.replaceAll(REGEX_CARE_OF_LABLE, "")
					    .replaceAll(REGEX_SALUTATION, "")
						.replaceAll(OTHER, "")
						.replaceAll(REGEX_SPECIAL_CHARACTERS, "")
						.replaceAll(REGEX_WHITE_SPACE, " ")
						.replaceAll(APARTMENT, "apt")
						.replaceAll(STREET, "st")
						.replaceAll(ROAD, "rd")
						.replaceAll(MAIN, "mn")
						.replaceAll(CROSS, "crs")
						.replaceAll(SECTOR, "sec")
						.replaceAll(OPPOSITE, "opp")
						.replaceAll(MARKET, "mkt")
						.replaceAll("1[sS][tT]", "1")
						.replaceAll("1[tT][hH]", "1")
						.replaceAll("2[nN][dD]", "2")
						.replaceAll("2[tT][hH]", "2")
						.replaceAll("3[rR][dD]", "3")
						.replaceAll("3[tT][hH]", "3")
						.replaceAll("4[tT][hH]", "4")
						.replaceAll("5[tT][hH]", "5")
						.replaceAll("6[tT][hH]", "6")
						.replaceAll("7[tT][hH]", "7")
						.replaceAll("8[tT][hH]", "8")
						.replaceAll("9[tT][hH]", "9")
						.replaceAll("0[tT][hH]", "0")
						.trim();
		
		return address;
	}
}
