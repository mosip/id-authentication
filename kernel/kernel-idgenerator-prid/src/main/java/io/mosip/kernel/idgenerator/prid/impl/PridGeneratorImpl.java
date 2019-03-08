package io.mosip.kernel.idgenerator.prid.impl;

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
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.idgenerator.prid.entity.Prid;
import io.mosip.kernel.idgenerator.prid.exception.PridException;
import io.mosip.kernel.idgenerator.prid.repository.PridRepository;
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
@Transactional
public class PridGeneratorImpl implements PridGenerator<String> {

	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	@Autowired
	private PridRepository repository;

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
	private String generateRandomId() {

		String counterSecureRandom = null;

		String random = RandomStringUtils.random(32, "1234567890");

		String prid = null;

		List<Prid> listOfEntity = null;

		try {
			listOfEntity = repository.findRandomValues();

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new PridException("KER-PRID-001", "Error occur while fetching from db", e);
		}
		try {
			Prid entity = new Prid();

			if (listOfEntity.isEmpty()) {

				counterSecureRandom = RandomStringUtils.random(32, "1234567890");

				entity.setRandomValue(random);

				entity.setSequenceCounter(counterSecureRandom);

				repository.save(entity);

			} else {

				counterSecureRandom = listOfEntity.get(0).getSequenceCounter();

				random = listOfEntity.get(0).getRandomValue();

				repository.updateCounterValue(counterSecureRandom, random);

			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new PridException("KER-PRID-002", "Error occur while updating database", e);
		}

		counterSecureRandom = new BigInteger(counterSecureRandom).add(BigInteger.ONE).toString();

		SecretKey secretKey = new SecretKeySpec(counterSecureRandom.getBytes(), "AES");

		byte[] encryptedData = encryptor.symmetricEncrypt(secretKey, random.getBytes());

		BigInteger b = new BigInteger(encryptedData);

		prid = String.valueOf(b.abs());

		prid = prid.substring(0, pridLength - 1);

		String verhoeffDigit = ChecksumUtils.generateChecksumDigit(prid);

		return appendChecksum(prid, verhoeffDigit);

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
	private String appendChecksum(String generatedVId, String verhoeffDigit) {
		StringBuilder vidSb = new StringBuilder();
		vidSb.setLength(pridLength);
		return vidSb.insert(0, generatedVId).insert(generatedVId.length(), verhoeffDigit).toString().trim();
	}

}
