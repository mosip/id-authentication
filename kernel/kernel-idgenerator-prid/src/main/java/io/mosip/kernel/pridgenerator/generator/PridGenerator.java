package io.mosip.kernel.pridgenerator.generator;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.idgenerator.MosipPridGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.core.util.IdFilterUtils;
import io.mosip.kernel.pridgenerator.cache.PridCacheManager;
import io.mosip.kernel.pridgenerator.constant.PridGeneratorConstants;
import io.mosip.kernel.pridgenerator.constant.PridGeneratorErrorCodes;
import io.mosip.kernel.pridgenerator.entity.Prid;
import io.mosip.kernel.pridgenerator.exception.PridGenerationException;
import io.mosip.kernel.pridgenerator.repository.PridRepository;

/**
 * PridGenerator to generate PRID This class will return a Fourteen digit PRID
 * after the validation from MosipIdFilter
 * 
 * @author M1037462
 * @since 1.0.0
 *
 */
@Component
public class PridGenerator implements MosipPridGenerator<String> {
	@Value("${mosip.kernel.prid.length}")
	private int pridLength;
	@Autowired
	private PridRepository pridRepository;
	@Autowired
	private PridCacheManager pridCacheManager;

	private static final RandomDataGenerator RANDOM_DATA_GENERATOR = new RandomDataGenerator();

	private int generatedIdLength;
	private long lowerBound;
	private long upperBound;

	@PostConstruct
	public void pridGeneratorPostConstruct() {
		generatedIdLength = pridLength - 1;
		lowerBound = Long.parseLong(PridGeneratorConstants.TWO + StringUtils.repeat(PridGeneratorConstants.ZERO, generatedIdLength - 1));
		upperBound = Long.parseLong(StringUtils.repeat(PridGeneratorConstants.NINE, generatedIdLength));
	}


	@Override
	public String generateId() {
		Prid prid = new Prid();
		boolean unique = false;
		String generatedPrid = null;
		while (!unique) {
			generatedPrid = this.generatePrid();
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
			pridRepository.save(prid);
			pridCacheManager.add(prid.getId());
			return generatedPrid;
		} catch (Exception e) {
			throw new PridGenerationException(PridGeneratorErrorCodes.UNABLE_TO_CONNECT_TO_DB.getErrorCode(),
					PridGeneratorErrorCodes.UNABLE_TO_CONNECT_TO_DB.getErrorMessage());
		}
	}

	private String generatePrid() {
		String generatedPrid = generateRandomId(generatedIdLength, lowerBound, upperBound);
		while (!IdFilterUtils.isValidId(generatedPrid)) {
			generatedPrid = generatePrid();
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
		String verhoeffDigit = ChecksumUtils.generateChecksumDigit(String.valueOf(generatedID));
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
