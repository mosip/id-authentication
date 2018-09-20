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
 * This class generates a list of uins
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class UinGenerator implements MosipIdGenerator<Set<UinBean>> {

	/**
	 * The logger instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UinGenerator.class);

	/**
	 * Field for number of uins to generate
	 */
	private final long uinsCount;

	/**
	 * The length of the uin
	 */
	private final int uinLength;

	/**
	 * Constructor to set {@link #uinsCount} and {@link #uinLength}
	 * 
	 * @param uinsCount
	 *            The number of uins to generate
	 * @param uinLength
	 *            The length of the uin
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
	 * Generates a id and then generate checksum
	 * 
	 * @param generatedIdLength
	 *            The length of id to generate
	 * @param lowerBound
	 *            The lowerbound for generating id
	 * @param upperBound
	 *            The upperbound for generating id
	 * @return the uin with checksum
	 */
	private String generateSingleId(int generatedIdLength, long lowerBound, long upperBound) {
		Long generatedID = RANDOM_DATA_GENERATOR.nextSecureLong(lowerBound, upperBound);
		String verhoeffDigit = MosipIdChecksum.generateChecksumDigit(String.valueOf(generatedID));
		return appendChecksum(generatedIdLength, generatedID, verhoeffDigit);
	}

	/**
	 * Appends a checksum to generated id
	 * 
	 * @param generatedIdLength
	 *            The length of id
	 * @param generatedID
	 *            The generated id
	 * @param verhoeffDigit
	 *            The checksum to append
	 * @return uin with checksum
	 */
	private String appendChecksum(int generatedIdLength, Long generatedID, String verhoeffDigit) {
		StringBuilder uinStringBuilder = new StringBuilder();
		uinStringBuilder.setLength(uinLength);
		return uinStringBuilder.insert(0, generatedID).insert(generatedIdLength, verhoeffDigit).toString().trim();
	}
}
