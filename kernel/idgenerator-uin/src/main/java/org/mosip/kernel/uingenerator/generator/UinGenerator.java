package org.mosip.kernel.uingenerator.generator;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.mosip.kernel.core.spi.idgenerator.MosipIdGenerator;
import org.mosip.kernel.core.utils.MosipIdChecksum;
import org.mosip.kernel.core.utils.MosipIdFilter;
import org.mosip.kernel.uingenerator.constants.UinGeneratorConstants;
import org.mosip.kernel.uingenerator.model.UinBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class UinGenerator implements MosipIdGenerator<Set<UinBean>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UinGenerator.class);

	private final long uinsCount;

	private final int uinLength;

	/**
	 * @param uinsCount
	 * @param uinLength
	 */
	public UinGenerator(@Value("${uins.to.generate}") long uinsCount, @Value("${uin.length}") int uinLength) {
		this.uinsCount = uinsCount;
		this.uinLength = uinLength;
	}

	private static final RandomDataGenerator RANDOM_DATA_GENERATOR = new RandomDataGenerator();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.kernel.core.spi.idgenerator.MosipIdGenerator#generateId()
	 */
	@Override
	public Set<UinBean> generateId() {

		int generatedIdLength = uinLength - 1;
		Set<UinBean> uins = new HashSet<>();
		long upperBound = Long.parseLong(StringUtils.repeat(UinGeneratorConstants.NINE, generatedIdLength));
		long lowerBound = Long.parseLong(
				UinGeneratorConstants.TWO + StringUtils.repeat(UinGeneratorConstants.ZERO, generatedIdLength - 1));
		LOGGER.info("Generating {} uins ", uinsCount);
		while (uins.size() < uinsCount) {
			String generatedUIN = generateSingleId(generatedIdLength, lowerBound, upperBound);
			if (MosipIdFilter.isValidId(generatedUIN)) {
				UinBean uinBean = new UinBean(generatedUIN, false);
				uins.add(uinBean);
			}
		}
		LOGGER.info("Generated {} uins ", uinsCount);
		return uins;
	}

	/**
	 * @param generatedIdLength
	 * @param lowerBound
	 * @param upperBound
	 * @return
	 */
	private String generateSingleId(int generatedIdLength, long lowerBound, long upperBound) {
		Long generatedID = RANDOM_DATA_GENERATOR.nextSecureLong(lowerBound, upperBound);
		String verhoeffDigit = MosipIdChecksum.generateChecksumDigit(String.valueOf(generatedID));
		return appendChecksum(generatedIdLength, generatedID, verhoeffDigit);
	}

	/**
	 * @param generatedIdLength
	 * @param generatedID
	 * @param verhoeffDigit
	 * @return
	 */
	private String appendChecksum(int generatedIdLength, Long generatedID, String verhoeffDigit) {
		StringBuilder uinStringBuilder = new StringBuilder();
		uinStringBuilder.setLength(uinLength);
		return uinStringBuilder.insert(0, generatedID).insert(generatedIdLength, verhoeffDigit).toString().trim();
	}
}
