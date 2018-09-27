/**
 * 
 */
package org.mosip.kernel.vidgenerator.generator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.mosip.kernel.core.spi.idgenerator.MosipIdGenerator;
import org.mosip.kernel.core.utils.MosipIdChecksum;
import org.mosip.kernel.core.utils.MosipIdFilter;
import org.mosip.kernel.vidgenerator.cache.VidCacheManager;
import org.mosip.kernel.vidgenerator.constants.VIDErrorCodes;
import org.mosip.kernel.vidgenerator.constants.VIdGeneratorConstants;
import org.mosip.kernel.vidgenerator.dao.VidDao;
import org.mosip.kernel.vidgenerator.exception.InValidUinException;
import org.mosip.kernel.vidgenerator.exception.VIDGenerationFailedException;
import org.mosip.kernel.vidgenerator.model.VId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This class generates a VId
 * 
 * @author M1043226
 * @since 1.0.0
 *
 */

@Service
public class VidGenerator implements MosipIdGenerator<String> {

	/**
	 * The logger instance
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(VidGenerator.class);

	@Autowired
	VidDao vidDao;

	@Autowired
	VidCacheManager vidCacheManager;

	/**
	 * VId Validity in hour
	 */
	@Value("${kernel.vid.validity-in-hr}")
	private int vIdValidityHr;

	/**
	 * The length of the VId
	 */
	@Value("${kernel.vid.length}")
	private int vidLength;

	private static final RandomDataGenerator RANDOM_DATA_GENERATOR = new RandomDataGenerator();

	/**
	 * Generates a Vid and map it against the input Uin
	 * 
	 * @param uin
	 *            The requested uin
	 * @return a Vid
	 */
	public String generateId(String uin) {

		String generatedVid = null;

		if (uin == null) {
			throw new InValidUinException(VIDErrorCodes.INVALID_UIN.getErrorCode(),
					VIDErrorCodes.INVALID_UIN.getErrorMessage());
		} else {
			if (vidCacheManager.containsUin(uin)) {
				VId existingVId = vidCacheManager.findByUin(uin);
				long existingVIdCreatedAt = existingVId.getCreatedAt();
				if (existingVIdCreatedAt > (System.currentTimeMillis()
						- vIdValidityHr * VIdGeneratorConstants.MILLIS_IN_HR)) {
					LOGGER.info("If Vid not expired");
					return existingVId.getVid();
				} else {
					LOGGER.info("If Vid expired");
					generatedVid = getUniqueVid();
					long currentTimestamp = System.currentTimeMillis();

					existingVId.setVid(generatedVid);
					existingVId.setCreatedAt(currentTimestamp);

					try {
						vidDao.save(existingVId);
						vidCacheManager.saveOrUpdate(existingVId);
					} catch (Exception e) {
						throw new VIDGenerationFailedException(VIDErrorCodes.VID_GENERATION_FAILED.getErrorCode(),
								VIDErrorCodes.VID_GENERATION_FAILED.getErrorMessage());
					}

				}
			} else {
				LOGGER.info("If UIn is InValid");
				generatedVid = getUniqueVid();
				VId newVid = new VId();
				long currentTimestamp = System.currentTimeMillis();

				newVid.setUin(uin);
				newVid.setVid(generatedVid);
				newVid.setCreatedAt(currentTimestamp);
				try {
					vidDao.save(newVid);
					vidCacheManager.saveOrUpdate(newVid);
				} catch (Exception e) {
					throw new VIDGenerationFailedException(VIDErrorCodes.VID_GENERATION_FAILED.getErrorCode(),
							VIDErrorCodes.VID_GENERATION_FAILED.getErrorMessage());
				}
			}

		}
		return generatedVid;

	}

	/**
	 * Generates a unique Vid
	 */
	private String getUniqueVid() {
		boolean unique = false;
		String generatedVid = null;
		while (!unique) {
			generatedVid = this.generateId();
			if (vidCacheManager.containsVid(generatedVid)) {
				unique = false;
			} else {
				unique = true;
			}

		}
		return generatedVid;
	}

	/**
	 * Generates Id
	 */
	@Override
	public String generateId() {
		int generatedIdLength = vidLength - 1;
		long lowerBound = Long.parseLong(
				VIdGeneratorConstants.TWO + StringUtils.repeat(VIdGeneratorConstants.ZERO, generatedIdLength - 1));
		long upperBound = Long.parseLong(StringUtils.repeat(VIdGeneratorConstants.NINE, generatedIdLength));
		String generatedVID = generateVId(generatedIdLength, lowerBound, upperBound);
		while (!MosipIdFilter.isValidId(generatedVID)) {
			generatedVID = generateId();
		}
		return generatedVID;
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
	private String generateVId(int generatedIdLength, long lowerBound, long upperBound) {
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
	 * @return VId with checksum
	 */
	private String appendChecksum(int generatedIdLength, Long generatedVId, String verhoeffDigit) {
		StringBuilder vidSb = new StringBuilder();
		vidSb.setLength(vidLength);
		return vidSb.insert(0, generatedVId).insert(generatedIdLength, verhoeffDigit).toString().trim();
	}
}
