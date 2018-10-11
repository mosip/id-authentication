package io.mosip.authentication.service.impl.indauth.service.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Generic class to normalize individual name, address.
 *
 * @author Rakesh Roshan
 */
@PropertySource(value = { "classpath:application-local.properties" })
public final class DemoNormalizer {

	@Autowired
	static Environment env;

	private static final String REGEX_SPECIAL_CHARACTERS = env.getProperty("Normalizer.special.characters");
	private static final String REGEX_SALUTATION = env.getProperty("Normalizer.salutations");
	private static final String REGEX_CARE_OF_LABLE = env.getProperty("Normalizer.careOfLables");
	private static final String OTHER = env.getProperty("Normalizer.other");
	private static final String REGEX_WHITE_SPACE = env.getProperty("Normalizer.whiteSpace");

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
