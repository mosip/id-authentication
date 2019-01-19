package io.mosip.kernel.idgenerator.uin.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idgenerator.spi.UinGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.idgenerator.uin.constant.UinGeneratorConstant;
import io.mosip.kernel.idgenerator.uin.entity.UinEntity;
import io.mosip.kernel.idgenerator.uin.util.UinFilterUtils;

/**
 * This class generates a list of uins
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class UinGeneratorImpl implements UinGenerator<Set<UinEntity>> {
	/**
	 * instance of {@link UinGeneratorImpl}
	 */
	@Autowired
	private UinFilterUtils uinFilterUtils;

	/**
	 * The logger instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UinGeneratorImpl.class);

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
	public UinGeneratorImpl(@Value("${mosip.kernel.uin.uins-to-generate}") long uinsCount,
			@Value("${mosip.kernel.uin.length}") int uinLength) {
		this.uinsCount = uinsCount;
		this.uinLength = uinLength;
	}

	private static final RandomDataGenerator RANDOM_DATA_GENERATOR = new RandomDataGenerator();

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.spi.idgenerator.IdGenerator#generateId()
	 */
	@Override
	public Set<UinEntity> generateId() {

		int generatedIdLength = uinLength - 1;
		Set<UinEntity> uins = new HashSet<>();
		long upperBound = Long.parseLong(StringUtils.repeat(UinGeneratorConstant.NINE, generatedIdLength));
		long lowerBound = Long.parseLong(
				UinGeneratorConstant.TWO + StringUtils.repeat(UinGeneratorConstant.ZERO, generatedIdLength - 1));
		LOGGER.info("Generating {} uins ", uinsCount);
		while (uins.size() < uinsCount) {
			String generatedUIN = generateSingleId(generatedIdLength, lowerBound, upperBound);
			if (uinFilterUtils.isValidId(generatedUIN)) {
				UinEntity uinBean = new UinEntity(generatedUIN, false);
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
		String verhoeffDigit = ChecksumUtils.generateChecksumDigit(String.valueOf(generatedID));
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
