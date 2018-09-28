package org.mosip.kernel.pridgenerator.generator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.mosip.kernel.core.spi.idgenerator.MosipIdGenerator;
import org.mosip.kernel.core.utils.MosipIdChecksum;
import org.mosip.kernel.core.utils.MosipIdFilter;
import org.mosip.kernel.pridgenerator.cache.PridCacheManager;
import org.mosip.kernel.pridgenerator.constants.PridGeneratorErrorCodes;
import org.mosip.kernel.pridgenerator.dao.PridGenRepository;
import org.mosip.kernel.pridgenerator.exception.PridGenerationException;
import org.mosip.kernel.pridgenerator.model.Prid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * PridGenerator to generate PRID This class will return a Fourteen digit PRID
 * after the validation from MosipIdFilter
 * 
 * @author M1037462
 * @since 1.0.0
 *
 */
@Component
public class PridGenerator implements MosipIdGenerator<String> {
	@Value("${kernel.prid.length}")
	private int pridLength;
	/**
	 * The string field 0
	 */
	public static final String ZERO = "0";
	/**
	 * The string field 2
	 */
	public static final String TWO = "2";
	/**
	 * The string field 9
	 */
	public static final String NINE = "9";
	@Autowired
	private PridGenRepository pridGenRepository;
	@Autowired
	private PridCacheManager pridCacheManager;
	private static final RandomDataGenerator RANDOM_DATA_GENERATOR = new RandomDataGenerator();





	public String generatePrid() {
		Prid prid = new Prid();
		boolean unique = false;
		String generatedPrid = null;
		while (!unique) {
			generatedPrid = this.generateId();
			if (pridCacheManager.contains(generatedPrid)) {
				unique = false;
			} else {
				unique = true;
			}
		}
		long currentTimestamp = System.currentTimeMillis();
		prid.setId(generatedPrid);
		prid.setCreatedAt(currentTimestamp);
		try {
			pridGenRepository.save(prid);
			pridCacheManager.add(prid.getId());
			return generatedPrid;
		} catch (Exception e) {
			throw new PridGenerationException(PridGeneratorErrorCodes.UNABLE_TO_CONNECT_TO_DB.getErrorCode(), PridGeneratorErrorCodes.UNABLE_TO_CONNECT_TO_DB.getErrorMessage());
		}
	}





	@Override
	public String generateId() {
		int generatedIdLength = pridLength - 1;
		long lowerBound = Long.parseLong(TWO + StringUtils.repeat(ZERO, generatedIdLength - 1));
		long upperBound = Long.parseLong(StringUtils.repeat(NINE, generatedIdLength));
		String generatedPrid = generatePrid(generatedIdLength, lowerBound, upperBound);
		while (!MosipIdFilter.isValidId(generatedPrid)) {
			generatedPrid = generateId();
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
	private String generatePrid(int generatedIdLength, long lowerBound, long upperBound) {
		Long generatedID = RANDOM_DATA_GENERATOR.nextSecureLong(lowerBound, upperBound);
		String verhoeffDigit = MosipIdChecksum.generateChecksumDigit(String.valueOf(generatedID));
		return appendChecksum(generatedIdLength, generatedID, verhoeffDigit);
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
	private String appendChecksum(int generatedIdLength, Long generatedVId, String verhoeffDigit) {
		StringBuilder vidSb = new StringBuilder();
		vidSb.setLength(pridLength);
		return vidSb.insert(0, generatedVId).insert(generatedIdLength, verhoeffDigit).toString().trim();
	}
}
