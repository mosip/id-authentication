package io.mosip.kernel.idgenerator.prid.impl;

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

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.idgenerator.prid.constant.PridExceptionConstant;
import io.mosip.kernel.idgenerator.prid.constant.PridPropertyConstant;
import io.mosip.kernel.idgenerator.prid.entity.PridSeed;
import io.mosip.kernel.idgenerator.prid.entity.PridSequence;
import io.mosip.kernel.idgenerator.prid.exception.PridException;
import io.mosip.kernel.idgenerator.prid.repository.PridSeedRepository;
import io.mosip.kernel.idgenerator.prid.repository.PridSequenceRepository;
import io.mosip.kernel.idgenerator.prid.util.PridFilterUtils;

/**
 * PridGenerator to generate PRID and generated PRID after the validation from
 * IdFilter
 * 
 * @author Rupika
 * @author Megha Tanga
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Component
public class PridGeneratorImpl implements PridGenerator<String> {

	@Autowired
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

	/**
	 * Reference to {@link PridSeedRepository}.
	 */
	@Autowired
	private PridSeedRepository seedRepository;

	/**
	 * Reference to {@link PridSequenceRepository}.
	 */
	@Autowired
	private PridSequenceRepository sequenceRepository;

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

	/**
	 * Generates a id and then validate a id
	 *
	 * @return return the generated PRID
	 */

	@Transactional(isolation = Isolation.READ_COMMITTED)
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
		String generatedPrid = generateRandomId();
		while (!pridFilterUtils.isValidId(generatedPrid)) {
			generatedPrid = generateRandomId();
		}
		return generatedPrid;
	}

	/**
	 * Generates an id and then generate checksum
	 * 
	 * @return the PRID with checksum
	 */
	private String generateRandomId() {

		String counterSecureRandom = null;
		String randomSeed = null;
		String prid = null;

		List<PridSeed> listOfSeed = null;
		PridSequence sequenceEntity = null;

		try {
			listOfSeed = seedRepository.findAll();
			sequenceEntity = sequenceRepository.findMaxSequence();

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new PridException(PridExceptionConstant.PRID_FETCH_EXCEPTION.getErrorCode(),
					PridExceptionConstant.PRID_FETCH_EXCEPTION.getErrorMessage(), e);
		}
		try {
			PridSequence counterEntity = new PridSequence();
			counterEntity.setCreatedBy("SYSTEM");
			counterEntity.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
			counterEntity.setDeletedDateTime(null);
			counterEntity.setIsDeleted(false);

			if (sequenceEntity == null) {
				do {
					counterSecureRandom = RandomStringUtils.random(
							Integer.parseInt(PridPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
							PridPropertyConstant.ZERO_TO_NINE.getProperty());
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
						Integer.parseInt(PridPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
						PridPropertyConstant.ZERO_TO_NINE.getProperty());
				PridSeed seedEntity = new PridSeed();
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
				throw new PridException(PridExceptionConstant.PRID_INSERTION_EXCEPTION.getErrorCode(),
						PridExceptionConstant.PRID_INSERTION_EXCEPTION.getErrorMessage(), e);
			}
		}

		SecretKey secretKey = new SecretKeySpec(counterSecureRandom.getBytes(),
				PridPropertyConstant.ENCRYPTION_ALGORITHM.getProperty());

		byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKey, randomSeed.getBytes(),null);

		BigInteger bigInteger = new BigInteger(encryptedData);

		prid = String.valueOf(bigInteger.abs());

		prid = prid.substring(0, pridLength - 1);

		String verhoeffDigit = ChecksumUtils.generateChecksumDigit(prid);

		return appendChecksum(prid, verhoeffDigit);

	}

	/**
	 * Appends a checksum to generated id
	 * 
	 * @param generatedIdLength The length of id
	 * @param generatedVId      The generated id
	 * @param verhoeffDigit     The checksum to append
	 */
	private String appendChecksum(String generatedVId, String verhoeffDigit) {
		StringBuilder vidSb = new StringBuilder();
		vidSb.setLength(pridLength);
		return vidSb.insert(0, generatedVId).insert(generatedVId.length(), verhoeffDigit).toString().trim();
	}

}
