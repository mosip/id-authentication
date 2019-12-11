package io.mosip.kernel.idgenerator.prid.impl;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.idgenerator.prid.constant.PridPropertyConstant;
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

	boolean init = true;

	private String randomSeed;

	private String counter;

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

	@PostConstruct
	private void init() {
		randomSeed = RandomStringUtils.random(Integer.parseInt(PridPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
				PridPropertyConstant.ZERO_TO_NINE.getProperty());

		do {
			counter = RandomStringUtils.random(Integer.parseInt(PridPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
					PridPropertyConstant.ZERO_TO_NINE.getProperty());
		} while (counter.charAt(0) == '0');
	}

	@Override
	public String generateId() {
		String generatedVid = generateRandomId();
		while (!pridFilterUtils.isValidId(generatedVid) || generatedVid.contains(" ")) {
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
		String prid = null;
		counter = init ? counter : new BigInteger(counter).add(BigInteger.ONE).toString();
		init = false;
		SecretKey secretKey = new SecretKeySpec(counter.getBytes(),
				PridPropertyConstant.ENCRYPTION_ALGORITHM.getProperty());
		byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKey, randomSeed.getBytes(), null);
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
	 * @param generatedID       The generated id
	 * @param verhoeffDigit     The checksum to append
	 * @return PRID with checksum
	 */
	private String appendChecksum(String generatedPrid, String verhoeffDigit) {
		StringBuilder pridStringbuilder = new StringBuilder();
		pridStringbuilder.setLength(pridLength);
		return pridStringbuilder.insert(0, generatedPrid).insert(generatedPrid.length(), verhoeffDigit).toString()
				.trim();
	}

}
