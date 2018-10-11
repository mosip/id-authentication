package io.mosip.authentication.service.impl.indauth.service.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DemoNormalizerTest {

	private static final String REGEX_SPECIAL_CHARACTERS = "[\\.|,|\\-|\\*|\\(|\\)|\\[|\\]|`|\\'|/|\\|#|\"]";
	private static final String REGEX_SALUTATION = "(M|m|D|d)(rs?)(.)";
	private static final String REGEX_CARE_OF_LABLE = "(C|c|S|s|D|d|W|w|H|h)/[O|o]";
	private static final String OTHER = "(N|n)(O|o)(\\.)?";
	private static final String REGEX_WHITE_SPACE = "\\s+";

	@Test
	public void testNameNormalizer() {
		String[] name = name();
		String normalizeName;

		for (String s : name) {
			normalizeName = s.replaceAll(REGEX_SALUTATION, "").replaceAll(REGEX_SPECIAL_CHARACTERS, " ")
					.replaceAll(REGEX_WHITE_SPACE, " ").trim();
			System.out.println(normalizeName);
		}
	}

	@Test
	public void testAddressNormalizer() {
		String[] address = addressLine1();
		String normalizeAddress;

		for (String s : address) {
			normalizeAddress = s.replaceAll(REGEX_CARE_OF_LABLE, "").replaceAll(REGEX_SALUTATION, "")
					.replaceAll(OTHER, "").replaceAll(REGEX_SPECIAL_CHARACTERS, " ").replaceAll(REGEX_WHITE_SPACE, " ")
					.trim();
			System.out.println(normalizeAddress);
		}
	}

	private String[] addressLine1() {

		return new String[] { "C/o- Mr.mosip,.*      mosip", "No. 5", "" };

	}

	private String[] name() {
		return new String[] { "Mr. mosip", "Mrs. *mosip", "Dr. mosip,.`' mosip", "Dr. mosip      mosip*" };
	}

}
