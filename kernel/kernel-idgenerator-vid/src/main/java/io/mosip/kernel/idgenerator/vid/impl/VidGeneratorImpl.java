/**
 * 
 */
package io.mosip.kernel.idgenerator.vid.impl;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.idgenerator.vid.constant.VidExceptionConstant;
import io.mosip.kernel.idgenerator.vid.constant.VidPropertyConstant;
import io.mosip.kernel.idgenerator.vid.entity.Vid;
import io.mosip.kernel.idgenerator.vid.exception.VidException;
import io.mosip.kernel.idgenerator.vid.repository.VidRepository;
import io.mosip.kernel.idgenerator.vid.util.VidFilterUtils;

/**
 * This class generates a Vid.
 * 
 * @author Tapaswini
 * @author Megha Tanga
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Component
@Transactional
public class VidGeneratorImpl implements VidGenerator<String> {

	/**
	 * Reference to {@link Encryptor}.
	 */
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	/**
	 * Reference to repository.
	 */
	@Autowired
	private VidRepository repository;

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
	 * This method Generates an id and then validate.
	 */

	private String generateVid() {
		String generatedVid = generateRandomId();
		while (!vidFilterUtils.isValidId(generatedVid) || generatedVid.contains(" ")) {
			generatedVid = generateRandomId();
		}
		return generatedVid;
	}

	/**
	 * Generates a id and then generate checksum
	 * 
	 * @param generatedIdLength
	 *            The length of id to generate
	 * @return the VId with checksum
	 */
	private String generateRandomId() {

		String counterSecureRandom = null;

		String random = RandomStringUtils.random(Integer.parseInt(VidPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
				VidPropertyConstant.ZERO_TO_NINE.getProperty());
		String vid = null;
		List<Vid> listOfEntity = null;
		try {
			listOfEntity = repository.findRandomValues();
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new VidException(VidExceptionConstant.VID_FETCH_EXCEPTION.getErrorCode(),
					VidExceptionConstant.VID_FETCH_EXCEPTION.getErrorMessage(), e);
		}
		try {
			Vid entity = new Vid();
			if (listOfEntity.isEmpty()) {
				do {
					counterSecureRandom = RandomStringUtils.random(
							Integer.parseInt(VidPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
							VidPropertyConstant.ZERO_TO_NINE.getProperty());
				} while (counterSecureRandom.charAt(0) == '0');
				entity.setRandomValue(random);
				entity.setSequenceCounter(counterSecureRandom);
				repository.save(entity);
			} else {
				counterSecureRandom = listOfEntity.get(0).getSequenceCounter();
				counterSecureRandom = new BigInteger(counterSecureRandom).add(BigInteger.ONE).toString();
				random = listOfEntity.get(0).getRandomValue();
				repository.updateCounterValue(counterSecureRandom, random);
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new VidException(VidExceptionConstant.VID_INSERTION_EXCEPTION.getErrorCode(),
					VidExceptionConstant.VID_INSERTION_EXCEPTION.getErrorMessage(), e);
		}

		SecretKey secretKey = new SecretKeySpec(counterSecureRandom.getBytes(),
				VidPropertyConstant.ENCRYPTION_ALGORITHM.getProperty());
		byte[] encryptedData = encryptor.symmetricEncrypt(secretKey, random.getBytes());
		BigInteger bigInteger = new BigInteger(encryptedData);
		vid = String.valueOf(bigInteger.abs());
		vid = vid.substring(0, vidLength - 1);
		String verhoeffDigit = ChecksumUtils.generateChecksumDigit(vid);
		return appendChecksum(vid, verhoeffDigit);
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
	private String appendChecksum(String generatedVId, String verhoeffDigit) {
		StringBuilder vidSb = new StringBuilder();
		vidSb.setLength(vidLength);
		return vidSb.insert(0, generatedVId).insert(generatedVId.length(), verhoeffDigit).toString().trim();
	}

}
