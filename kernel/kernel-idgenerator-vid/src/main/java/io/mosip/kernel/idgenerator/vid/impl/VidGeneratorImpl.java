package io.mosip.kernel.idgenerator.vid.impl;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.idgenerator.vid.constant.VidExceptionConstant;
import io.mosip.kernel.idgenerator.vid.constant.VidPropertyConstant;
import io.mosip.kernel.idgenerator.vid.entity.VidSeed;
import io.mosip.kernel.idgenerator.vid.entity.VidSequence;
import io.mosip.kernel.idgenerator.vid.exception.VidException;
import io.mosip.kernel.idgenerator.vid.repository.VidSeedRepository;
import io.mosip.kernel.idgenerator.vid.repository.VidSequenceRepository;
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
public class VidGeneratorImpl implements VidGenerator<String> {

	/**
	 * Reference to {@link Encryptor}.
	 */
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	/**
	 * Reference to {@link VidSeedRepository}.
	 */
	@Autowired
	private VidSeedRepository seedRepository;

	/**
	 * Reference to {@link VidSequenceRepository}.
	 */
	@Autowired
	private VidSequenceRepository sequenceRepository;
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
	@Transactional(isolation = Isolation.READ_COMMITTED)
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
	 * @param generatedIdLength The length of id to generate
	 * @return the VId with checksum
	 */
	private String generateRandomId() {

		String counterSecureRandom = null;

		String randomSeed = null;
		String vid = null;

		List<VidSeed> listOfSeed = null;
		VidSequence sequenceEntity = null;

		try {
			listOfSeed = seedRepository.findAll();
			sequenceEntity = sequenceRepository.findMaxSequence();

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new VidException(VidExceptionConstant.VID_FETCH_EXCEPTION.getErrorCode(),
					VidExceptionConstant.VID_FETCH_EXCEPTION.getErrorMessage(), e);
		}
		try {
			VidSequence counterEntity = new VidSequence();
			counterEntity.setCreatedBy("SYSTEM");
			counterEntity.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
			counterEntity.setDeletedDateTime(null);
			counterEntity.setIsDeleted(false);

			if (sequenceEntity == null) {
				do {
					counterSecureRandom = RandomStringUtils.random(
							Integer.parseInt(VidPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
							VidPropertyConstant.ZERO_TO_NINE.getProperty());
				} while (counterSecureRandom.charAt(0) == '0');
				counterEntity.setSequenceNumber(counterSecureRandom);
			} else {
				counterSecureRandom = sequenceEntity.getSequenceNumber();
				counterSecureRandom = new BigInteger(counterSecureRandom).add(BigInteger.ONE).toString();
				counterEntity.setSequenceNumber(counterSecureRandom);
			}

			sequenceRepository.saveAndFlush(counterEntity);

			if (listOfSeed.isEmpty()) {
				randomSeed = RandomStringUtils.random(
						Integer.parseInt(VidPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
						VidPropertyConstant.ZERO_TO_NINE.getProperty());
				VidSeed seedEntity = new VidSeed();
				seedEntity.setCreatedBy("SYSTEM");
				seedEntity.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
				seedEntity.setDeletedDateTime(null);
				seedEntity.setIsDeleted(false);
				seedEntity.setSeedNumber(randomSeed);
				seedRepository.saveAndFlush(seedEntity);
			} else {
				randomSeed = listOfSeed.get(0).getSeedNumber();
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			if (e.getCause().getClass() == PersistenceException.class) {
				return generateId();
			} else {
				throw new VidException(VidExceptionConstant.VID_INSERTION_EXCEPTION.getErrorCode(),
						VidExceptionConstant.VID_INSERTION_EXCEPTION.getErrorMessage(), e);
			}
		}

		SecretKey secretKey = new SecretKeySpec(counterSecureRandom.getBytes(),
				VidPropertyConstant.ENCRYPTION_ALGORITHM.getProperty());
		byte[] encryptedData = encryptor.symmetricEncrypt(secretKey, randomSeed.getBytes());
		BigInteger bigInteger = new BigInteger(encryptedData);
		vid = String.valueOf(bigInteger.abs());
		vid = vid.substring(0, vidLength - 1);
		String verhoeffDigit = ChecksumUtils.generateChecksumDigit(vid);
		return appendChecksum(vid, verhoeffDigit);
	}

	/**
	 * Appends a checksum to generated id
	 * 
	 * @param generatedIdLength The length of id
	 * @param generatedID       The generated id
	 * @param verhoeffDigit     The checksum to append
	 * @return VId with checksum
	 */
	private String appendChecksum(String generatedVId, String verhoeffDigit) {
		StringBuilder vidSb = new StringBuilder();
		vidSb.setLength(vidLength);
		return vidSb.insert(0, generatedVId).insert(generatedVId.length(), verhoeffDigit).toString().trim();
	}

}
