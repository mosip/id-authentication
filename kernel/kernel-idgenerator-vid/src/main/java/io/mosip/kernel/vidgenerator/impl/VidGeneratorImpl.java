/**
 * 
 */
package io.mosip.kernel.vidgenerator.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;

import io.mosip.kernel.vidgenerator.constant.VidGeneratorConstant;

import io.mosip.kernel.vidgenerator.util.VidFilterUtils;

/**
 * This class generates a VId
 * 
 * @author M1043226
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Service
public class VidGeneratorImpl implements VidGenerator<String> {
	
	/**
	 * Field to hold vidFilterUtils object
	 */
	@Autowired
	VidFilterUtils vidFilterUtils;

	/**
	 * The length of the VId
	 */
	@Value("${mosip.kernel.vid.length}")
	private int vidLength;
	private static final RandomDataGenerator RANDOM_DATA_GENERATOR = new RandomDataGenerator();

	private int generatedIdLength;
	private long lowerBound;
	private long upperBound;

	@PostConstruct
	public void vidGeneratorPostConstruct() {
		generatedIdLength = vidLength - 1;
		lowerBound = Long.parseLong(
				VidGeneratorConstant.TWO + StringUtils.repeat(VidGeneratorConstant.ZERO, generatedIdLength - 1));
		upperBound = Long.parseLong(StringUtils.repeat(VidGeneratorConstant.NINE, generatedIdLength));
	}

	/**
	 * Generates a Vid 
	 * 
	 * @return a vid
	 */
	@Override
	public String generateId() {
	
		return generateVid();
	}


	/**
	 * Generates Id
	 */

	private String generateVid() {
		String generatedVid = generateRandomId(generatedIdLength, lowerBound, upperBound);
		while (!vidFilterUtils.isValidId(generatedVid)) {
			generatedVid = generateRandomId(generatedIdLength, lowerBound, upperBound);
		}
		return generatedVid;
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
	 * @return the VId with checksum
	 */
	private String generateRandomId(int generatedIdLength, long lowerBound, long upperBound) {
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
	 * @return VId with checksum
	 */
	private String appendChecksum(int generatedIdLength, Long generatedVId, String verhoeffDigit) {
		StringBuilder vidSb = new StringBuilder();
		vidSb.setLength(vidLength);
		return vidSb.insert(0, generatedVId).insert(generatedIdLength, verhoeffDigit).toString().trim();
	}

}
