/**
 * 
 */
package io.mosip.kernel.vidgenerator.impl;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.exception.InValidUinException;
import io.mosip.kernel.core.idgenerator.exception.VidGenerationFailedException;
import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.vidgenerator.cache.VidCacheManager;
import io.mosip.kernel.vidgenerator.constant.VidErrorCode;
import io.mosip.kernel.vidgenerator.constant.VidGeneratorConstant;
import io.mosip.kernel.vidgenerator.entity.Vid;
import io.mosip.kernel.vidgenerator.repository.VidRepository;
import io.mosip.kernel.vidgenerator.util.VidFilterUtils;

/**
 * This class generates a VId
 * 
 * @author M1043226
 * @since 1.0.0
 *
 */
@Service
public class VidGeneratorImpl implements VidGenerator<String> {
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
		lowerBound = Long.parseLong(
				VidGeneratorConstant.TWO + StringUtils.repeat(VidGeneratorConstant.ZERO, generatedIdLength - 1));
		upperBound = Long.parseLong(StringUtils.repeat(VidGeneratorConstant.NINE, generatedIdLength));
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
			throw new InValidUinException(VidErrorCode.INVALID_UIN.getErrorCode(),
					VidErrorCode.INVALID_UIN.getErrorMessage());
		}
		if (vidCacheManager.containsUin(uin)) {
			Vid existingVId = vidCacheManager.findByUin(uin);
			long existingVIdCreatedAt = existingVId.getCreatedAt();
			if (existingVIdCreatedAt > (System.currentTimeMillis()
					- vidValidityHr * VidGeneratorConstant.MILLIS_IN_HR)) {
				return existingVId.getId();
			}
			generatedVid = getUniqueVid();
			long currentTimestamp = System.currentTimeMillis();
			existingVId.setId(generatedVid);
			existingVId.setCreatedAt(currentTimestamp);
			saveGeneratedVid(existingVId);
		} else {
			generatedVid = getUniqueVid();
			Vid newVid = new Vid();
			long currentTimestamp = System.currentTimeMillis();
			newVid.setUin(uin);
			newVid.setId(generatedVid);
			newVid.setCreatedAt(currentTimestamp);
			saveGeneratedVid(newVid);
		}
		return generatedVid;
	}

	private void saveGeneratedVid(Vid newVid) {
		try {
			vidRepository.save(newVid);
			vidCacheManager.saveOrUpdate(newVid);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new VidGenerationFailedException(VidErrorCode.VID_GENERATION_FAILED.getErrorCode(),
					VidErrorCode.VID_GENERATION_FAILED.getErrorMessage());
		}
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
		while (!VidFilterUtils.isValidId(generatedVid)) {
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
