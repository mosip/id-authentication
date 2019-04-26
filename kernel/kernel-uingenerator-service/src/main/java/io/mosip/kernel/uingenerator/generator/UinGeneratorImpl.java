package io.mosip.kernel.uingenerator.generator;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idgenerator.spi.UinGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.util.MetaDataUtil;
import io.mosip.kernel.uingenerator.util.UinFilterUtil;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * This class generates a list of uins
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class UinGeneratorImpl implements UinGenerator {
	/**
	 * instance of {@link UinFilterUtil}
	 */
	@Autowired
	private UinFilterUtil uinFilterUtils;

	/**
	 * instance of {@link MetaDataUtil}
	 */
	@Autowired
	private MetaDataUtil metaDataUtil;

	/**
	 * Field for UinWriter
	 */
	@Autowired
	private UinWriter uinWriter;

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
	 * The uin default status
	 */
	private final String uinDefaultStatus;

	/**
	 * Constructor to set {@link #uinsCount} and {@link #uinLength}
	 * 
	 * @param uinsCount        The number of uins to generate
	 * @param uinLength        The length of the uin
	 * @param uinDefaultStatus The Default value of the uin
	 */
	public UinGeneratorImpl(@Value("${mosip.kernel.uin.uins-to-generate}") long uinsCount,
			@Value("${mosip.kernel.uin.length}") int uinLength) {
		this.uinsCount = uinsCount;
		this.uinLength = uinLength;
		this.uinDefaultStatus = UinGeneratorConstant.UNUSED;
	}

	private static final RandomDataGenerator RANDOM_DATA_GENERATOR = new RandomDataGenerator();

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.spi.idgenerator.IdGenerator#generateId()
	 */
	@Override
	public void generateId() {
		int generatedIdLength = uinLength - 1;
		long uinCount = 0;
		long upperBound = Long.parseLong(StringUtils.repeat(UinGeneratorConstant.NINE, generatedIdLength));
		long lowerBound = Long.parseLong(StringUtils.repeat(UinGeneratorConstant.ZERO, generatedIdLength));
		uinWriter.setSession();
		while (uinCount < uinsCount) {
			String generatedUIN = generateSingleId(generatedIdLength, lowerBound, upperBound);
			if (uinFilterUtils.isValidId(generatedUIN)) {
				UinEntity uinBean = new UinEntity(generatedUIN, uinDefaultStatus);
				metaDataUtil.setCreateMetaData(uinBean);
				// try {
				uinWriter.persistUin(uinBean);
				uinCount++;
				/*
				 * } catch (Exception e) { //Skinping on PK violation e.printStackTrace(); }
				 */
			}
		}
		uinWriter.closeSession();
		//LOGGER.info("Generated {} uins ", uinsCount);
	}

	/**
	 * Generates a id and then generate checksum
	 * 
	 * @param generatedIdLength The length of id to generate
	 * @param lowerBound        The lowerbound for generating id
	 * @param upperBound        The upperbound for generating id
	 * @return the uin with checksum
	 */
	private String generateSingleId(int generatedIdLength, long lowerBound, long upperBound) {
		String generatedID = RandomStringUtils.random(generatedIdLength, UinGeneratorConstant.ZERO_TO_NINE);
		String verhoeffDigit = ChecksumUtils.generateChecksumDigit(String.valueOf(generatedID));
		return appendChecksum(generatedIdLength, generatedID, verhoeffDigit);
	}

	/**
	 * Appends a checksum to generated id
	 * 
	 * @param generatedIdLength The length of id
	 * @param generatedID       The generated id
	 * @param verhoeffDigit     The checksum to append
	 * @return uin with checksum
	 */
	private String appendChecksum(int generatedIdLength, String generatedID, String verhoeffDigit) {
		StringBuilder uinStringBuilder = new StringBuilder();
		uinStringBuilder.setLength(uinLength);
		return uinStringBuilder.insert(0, generatedID).insert(generatedID.length(), verhoeffDigit).toString().trim();
	}

}
