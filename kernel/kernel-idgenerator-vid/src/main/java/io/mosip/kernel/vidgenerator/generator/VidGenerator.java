/**
 * 
 */
package io.mosip.kernel.vidgenerator.generator;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.spi.idgenerator.MosipVidGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.core.util.IdFilterUtils;
import io.mosip.kernel.vidgenerator.cache.VidCacheManager;
import io.mosip.kernel.vidgenerator.constant.VidErrorCodes;
import io.mosip.kernel.vidgenerator.constant.VidGeneratorConstants;
import io.mosip.kernel.vidgenerator.entity.Vid;
import io.mosip.kernel.vidgenerator.exception.InValidUinException;
import io.mosip.kernel.vidgenerator.exception.VidGenerationFailedException;
import io.mosip.kernel.vidgenerator.repository.VidRepository;

/**
 * This class generates a VId
 * 
 * @author M1043226
 * @since 1.0.0
 *
 */
@Service
public class VidGenerator implements MosipVidGenerator<String> {
	@Autowired
	VidRepository vidRepository;
	@Autowired
	VidCacheManager vidCacheManager;
	/**
	 * VId Validity in hour
	 */
	@Value("${mosip.kernel.vid.validity-in-hr}")
	private int vidValidityHr;
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
		lowerBound = Long.parseLong(VidGeneratorConstants.TWO + StringUtils.repeat(VidGeneratorConstants.ZERO, generatedIdLength - 1));
		upperBound = Long.parseLong(StringUtils.repeat(VidGeneratorConstants.NINE, generatedIdLength));
	}



	/**
	 * Generates a Vid and map it against the input Uin
	 * 
	 * @param uin
	 *            The requested uin
	 * @return a vid
	 */
	@Override
	public String generateId(String uin) {
		String generatedVid = null;
		if (uin == null) {
			throw new InValidUinException(VidErrorCodes.INVALID_UIN.getErrorCode(),
					VidErrorCodes.INVALID_UIN.getErrorMessage());
		}
		if (vidCacheManager.containsUin(uin)) {
			Vid existingVId = vidCacheManager.findByUin(uin);
			long existingVIdCreatedAt = existingVId.getCreatedAt();
			if (existingVIdCreatedAt > (System.currentTimeMillis()
					- vidValidityHr * VidGeneratorConstants.MILLIS_IN_HR)) {
				return existingVId.getId();
			}
			generatedVid = getUniqueVid();
			long currentTimestamp = System.currentTimeMillis();
			existingVId.setId(generatedVid);
			existingVId.setCreatedAt(currentTimestamp);
			try {
				vidRepository.save(existingVId);
				vidCacheManager.saveOrUpdate(existingVId);
			} catch (Exception e) {
				throw new VidGenerationFailedException(VidErrorCodes.VID_GENERATION_FAILED.getErrorCode(),
						VidErrorCodes.VID_GENERATION_FAILED.getErrorMessage());
			}
		} else {
			generatedVid = getUniqueVid();
			Vid newVid = new Vid();
			long currentTimestamp = System.currentTimeMillis();
			newVid.setUin(uin);
			newVid.setId(generatedVid);
			newVid.setCreatedAt(currentTimestamp);
			try {
				vidRepository.save(newVid);
				vidCacheManager.saveOrUpdate(newVid);
			} catch (Exception e) {
				throw new VidGenerationFailedException(VidErrorCodes.VID_GENERATION_FAILED.getErrorCode(),
						VidErrorCodes.VID_GENERATION_FAILED.getErrorMessage());
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
			generatedVid = this.generateVid();
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

	private String generateVid() {
		String generatedVid = generateRandomId(generatedIdLength, lowerBound, upperBound);
		while (!IdFilterUtils.isValidId(generatedVid)) {
			generatedVid = generateVid();
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
