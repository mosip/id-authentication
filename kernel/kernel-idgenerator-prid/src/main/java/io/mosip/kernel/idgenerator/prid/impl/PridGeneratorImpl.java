package io.mosip.kernel.idgenerator.prid.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.idgenerator.prid.constant.PridGeneratorConstants;
import io.mosip.kernel.idgenerator.prid.util.PridFilterUtils;

/**
 * PridGenerator to generate PRID and generated PRID after the validation from
 * IdFilter
 * 
 * @author M1037462
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@Component
public class PridGeneratorImpl implements PridGenerator<String> {

	/**
	 * Field to hold PridFilterUtils object
	 */
	@Autowired
	PridFilterUtils pridFilterUtils;

	/**
	 * Field that takes Integer.This field decides the length of the PRID. It is
	 * read from the properties file.
	 */
	@Value("${mosip.kernel.prid.length}")
	private int pridLength;

	private static final RandomDataGenerator RANDOM_DATA_GENERATOR = new RandomDataGenerator();

	private int generatedIdLength;
	private long lowerBound;
	private long upperBound;

	/**
	 * Calculating PRID Length and lower Bound and upper Bound
	 * 
	 */
	@PostConstruct
	public void pridGeneratorPostConstruct() {
		generatedIdLength = pridLength - 1;
		lowerBound = Long.parseLong(
				PridGeneratorConstants.ZERO + StringUtils.repeat(PridGeneratorConstants.ZERO, generatedIdLength - 1));
		upperBound = Long.parseLong(StringUtils.repeat(PridGeneratorConstants.NINE, generatedIdLength));
	}

	/**
	 * Generates a id and then validate a id
	 *
	 * @return return the generated PRID
	 */
	@Override
	public String generateId() {
		return generatePrid();
	}

	/**
	 * Generates a id and then validate a id
	 *
	 * @return the PRID with checksum
	 */
	private String generatePrid() {
		String generatedPrid = generateRandomId(generatedIdLength, lowerBound, upperBound);
		while (!pridFilterUtils.isValidId(generatedPrid)) {
			generatedPrid = generateRandomId(generatedIdLength, lowerBound, upperBound);
		}
		return generatedPrid;
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
	 * @return the PRID with checksum
	 */
	private String generateRandomId(int generatedIdLength, long lowerBound, long upperBound) {
		Long generatedID = RANDOM_DATA_GENERATOR.nextSecureLong(lowerBound, upperBound);
		String id = String.valueOf(generatedID);
		String verhoeffDigit = ChecksumUtils.generateChecksumDigit(id);
		return appendChecksum(id, verhoeffDigit);
	}

	/**
	 * Appends a checksum to generated id
	 * 
	 * @param generatedIdLength
	 *            The length of id
	 * @param generatedVId
	 *            The generated id
	 * @param verhoeffDigit
	 *            The checksum to append
	 */
	private String appendChecksum(String generatedVId, String verhoeffDigit) {
		StringBuilder vidSb = new StringBuilder();
		vidSb.setLength(pridLength);
		return vidSb.insert(0, generatedVId).insert(generatedVId.length(), verhoeffDigit).toString().trim();
	}

}
