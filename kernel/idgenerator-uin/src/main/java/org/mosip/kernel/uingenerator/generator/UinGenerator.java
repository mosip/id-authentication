package org.mosip.kernel.uingenerator.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.mosip.kernel.core.spi.idgenerator.MosipIdGenerator;
import org.mosip.kernel.core.utils.MosipIdChecksum;
import org.mosip.kernel.core.utils.MosipIdFilter;
import org.mosip.kernel.uingenerator.model.UinBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UinGenerator implements MosipIdGenerator<Set<UinBean>> {
	private static final Logger log = LoggerFactory.getLogger(UinGenerator.class);

	@Value("${uins.to.generate}")
	private long uinsToGenerate;
	private static final int UIN_LENGTH = 12;
	private static final int CHECKSUM_POSITION = UIN_LENGTH;
	private static final int A_GROUP_LENGTH = 2;
	private static final int B_GROUP_LENGTH = 3;
	private static final int C_GROUP_LENGTH = UIN_LENGTH - A_GROUP_LENGTH - B_GROUP_LENGTH - 1;
	private static HashMap<Long, RandomDataGenerator> map = new HashMap<>();

	@Override
	public Set<UinBean> generateId() {

		String generatedUIN;
		Set<UinBean> uins = new HashSet<>();

		log.info("Generating {} uins ", uinsToGenerate);
		while (uins.size() < uinsToGenerate) {

			generatedUIN = generateSingleId();

			if (MosipIdFilter.isValidId(generatedUIN)) {
				UinBean uinBean = new UinBean(generatedUIN, false);
				uins.add(uinBean);
			}
		}
		return uins;
	}

	private String generateSingleId() {
		int groupA;
		int groupB;
		int groupC;
		String generatedID;
		String generatedUIN;
		long numericUuid = UUID.randomUUID().getMostSignificantBits();
		groupA = randomNumberGenerator(numericUuid, A_GROUP_LENGTH, false);
		groupB = randomNumberGenerator(groupA, B_GROUP_LENGTH, true);
		groupC = randomNumberGenerator(groupB, C_GROUP_LENGTH, true);

		generatedID = String.valueOf(groupA) + String.valueOf(groupB) + String.valueOf(groupC);

		String verhoeffDigit = MosipIdChecksum.generateChecksumDigit(generatedID);
		StringBuilder uinStringBuilder = new StringBuilder();
		uinStringBuilder.setLength(UIN_LENGTH);
		generatedUIN = uinStringBuilder.insert(0, generatedID).insert(CHECKSUM_POSITION - 1, verhoeffDigit).toString()
				.trim();
		return generatedUIN;
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
