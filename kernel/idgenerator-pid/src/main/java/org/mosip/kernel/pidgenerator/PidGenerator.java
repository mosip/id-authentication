/**
 * 
 */
package org.mosip.kernel.pidgenerator;

import java.util.Random;

import org.mosip.kernel.core.spi.idgenerator.MosipIdGenerator;
import org.mosip.kernel.core.utils.MosipIdChecksum;
import org.mosip.kernel.core.utils.MosipIdFilter;
import org.mosip.kernel.pidgenerator.exception.PidGenerationException;

/**
 * PidGenerator to generate PID 
 * This class will return a Fourteen digit PID after the validation from MosipIdFilter 
 * in which ten digit number from
 * currentTimeMillis() , three digit from Random() and 1 digit with Checksum
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public class PidGenerator implements MosipIdGenerator<String> {

	/**
	 * Limit the size of PID to Fourteen digit
	 */
	private static final int ID_LIMIT = 14;

	/**
	 * Gives the position of Checksum
	 */
	private static final int CHECKSUM_POSITION_ = ID_LIMIT - 1;

	/**
	 * Create instance of Random() to generate random number
	 */
	private static final Random random = new Random();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.kernel.core.spi.idgenerator.MosipIdGenerator#generateId()
	 */
	@Override
	public String generateId() {

		String generatedId = pidWithChecksum();

		while (!MosipIdFilter.isValidId(generatedId)) {

			generatedId = pidWithChecksum();
		}
		return generatedId;

	}

	/**
	 * It will generate Fourteen digit id with last digit checksum
	 * @return Fourteen digit ID with Checksum
	 */
	private String pidWithChecksum() {

		String generatorId;

		generatorId = idGenerator();

		String verhoeffDigit = MosipIdChecksum.generateChecksumDigit(generatorId);
		StringBuilder pidStringBuilder = new StringBuilder();
		pidStringBuilder.setLength(ID_LIMIT);

		return pidStringBuilder.insert(0, generatorId).insert(CHECKSUM_POSITION_, verhoeffDigit).toString().trim();

	}

	/**
	 * It will generate Thirteen digit ID with combination of Ten currentTimeMillis()
	 * and Three Random()
	 * 
	 * @return Thirteen digit ID
	 * @throws PidGenerationException when not able to generate The digit ID
	 */
	private String idGenerator() throws PidGenerationException {

		StringBuilder pidStringBuilder = new StringBuilder();
		String generatedId = Long.toString(System.currentTimeMillis());

		String tenDigitNumber = generatedId.substring(3, 13);
		String threeDigitNumber = genereteRandomNumber();

		pidStringBuilder.insert(0, tenDigitNumber).insert(10, threeDigitNumber);

		return pidStringBuilder.toString().trim();

	}

	/**
	 * It will generate Three digit random number
	 * 
	 * @return Three digit generated random number
	 */
	private String genereteRandomNumber() {

		String randomNumber = Integer.toString((random.nextInt(899) + 100));

		return randomNumber;

	}

}
