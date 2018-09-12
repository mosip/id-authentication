package org.mosip.kernel.uingenerator.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.mosip.kernel.uingenerator.model.UinBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UinGenerator {
	private static final Logger log = LoggerFactory.getLogger(UinGenerator.class);
	
	@Value("${uins.to.generate}")
	private long uinsToGenerate;
	private static final int UIN_LENGTH = 12;
	private static final int CHECKSUM_POSITION = UIN_LENGTH;
	private static final int REPEATING_LIMIT = 4;
	private static final int SEQUENCE_LIMIT = 4;
	private static final int REPEATING_BLOCK_LIMIT = 4;
	private static final String REPEATING_PATTERN = "(?=(\\d))\\1{" + REPEATING_LIMIT + ",}";
	private static final String REPEATING_BLOCK_PATTERN = "(\\d{" + REPEATING_BLOCK_LIMIT + ",}).*?\\1";
	private static final int A_GROUP_LENGTH = 2;
	private static final int B_GROUP_LENGTH = 3;
	private static final int C_GROUP_LENGTH = UIN_LENGTH - A_GROUP_LENGTH - B_GROUP_LENGTH - 1;
	private static HashMap<Long, RandomDataGenerator> map = new HashMap<>();

	public Set<UinBean> generate() {
		int groupA;
		int groupB;
		int groupC;
		String generatedID;
		String generatedUIN;
		Set<UinBean> uins = new HashSet<>();

		Matcher repeatingMatcher = Pattern.compile(REPEATING_PATTERN).matcher("");
		Matcher repeatingBlockMatcher = Pattern.compile(REPEATING_BLOCK_PATTERN).matcher("");
		log.info("Generating {} uins ", uinsToGenerate);
		while (uins.size() < uinsToGenerate) {

			long numericUuid = UUID.randomUUID().getMostSignificantBits();
			groupA = randomNumberGenerator(numericUuid, A_GROUP_LENGTH, false);
			groupB = randomNumberGenerator(groupA, B_GROUP_LENGTH, true);
			groupC = randomNumberGenerator(groupB, C_GROUP_LENGTH, true);

			generatedID = String.valueOf(groupA) + String.valueOf(groupB) + String.valueOf(groupC);

			String verhoeffDigit = Verhoeff.generateVerhoeffDigit(generatedID);
			StringBuilder uinStringBuilder = new StringBuilder();
			uinStringBuilder.setLength(UIN_LENGTH);
			generatedUIN = uinStringBuilder.insert(0, generatedID)
					.insert(CHECKSUM_POSITION - 1, verhoeffDigit).toString().trim();

			if (filterCheck(generatedUIN, repeatingMatcher, repeatingBlockMatcher)) {
				UinBean uinBean = new UinBean(generatedUIN, false);
				uins.add(uinBean);
			}
		}
		return uins;
	}

	private boolean filterCheck(String uin, Matcher repeatingMatcher, Matcher repeatingBlockMatcher) {
		return sequenceFilter(uin) && regexFilter(uin, repeatingMatcher) && regexFilter(uin, repeatingBlockMatcher);
	}

	private boolean sequenceFilter(String uinVal) {
		for (int uinSubCount = 0; uinSubCount <= uinVal.length() - SEQUENCE_LIMIT; uinSubCount++) {
			String uinSubString = uinVal.substring(uinSubCount, uinSubCount + SEQUENCE_LIMIT);
			if ("0123456789".contains(uinSubString) || "9876543210".contains(uinSubString)) {
				return false;
			}
		}
		return true;
	}

	private boolean regexFilter(String generatedID, Matcher matcher) {
		return !matcher.reset(generatedID).find();
	}

	private int randomNumberGenerator(long seed, int length, boolean saveSeedInMap) {
		RandomDataGenerator rand;
		int generatedNo;
		String max = "9" + StringUtils.repeat("9", length - 1);
		String min = "1" + StringUtils.repeat("0", length - 1);
		if (map.containsKey(seed)) {
			generatedNo = map.get(seed).nextSecureInt(Integer.parseInt(min), Integer.parseInt(max));
		} else {
			rand = new RandomDataGenerator();
			rand.reSeedSecure(seed);
			if (saveSeedInMap) {
				map.put(seed, rand);
			}
			generatedNo = rand.nextSecureInt(Integer.parseInt(min), Integer.parseInt(max));
		}
		return generatedNo;
	}
}
