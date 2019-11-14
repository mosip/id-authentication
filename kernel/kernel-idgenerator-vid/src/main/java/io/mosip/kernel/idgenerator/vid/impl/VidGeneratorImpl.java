package io.mosip.kernel.idgenerator.vid.impl;

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
import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.core.util.ChecksumUtils;
import io.mosip.kernel.idgenerator.vid.constant.VidPropertyConstant;
import io.mosip.kernel.idgenerator.vid.util.VidFilterUtils;

/**
 * This class generates a Vid.
 * 
 * @author Ritesh Sinha
 * @author Urvil Joshi
 * @author Megha Tanga
 * 
 * @since 1.0.0
 *
 */
@Component
public class VidGeneratorImpl implements VidGenerator<String> {

	boolean init = true;

	private String randomSeed;

	private String counter;

	@Autowired
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

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

	@PostConstruct
	private void init() {
		randomSeed = RandomStringUtils.random(Integer.parseInt(VidPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
				VidPropertyConstant.ZERO_TO_NINE.getProperty());

		do {
			counter = RandomStringUtils.random(Integer.parseInt(VidPropertyConstant.RANDOM_NUMBER_SIZE.getProperty()),
					VidPropertyConstant.ZERO_TO_NINE.getProperty());
		} while (counter.charAt(0) == '0');
	}

	/**
	 * Generates a Vid
	 * 
	 * @return a vid
	 */
	@Override
	public String generateId() {
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
		String vid = null;
		counter = init ? counter : new BigInteger(counter).add(BigInteger.ONE).toString();
		init = false;
		SecretKey secretKey = new SecretKeySpec(counter.getBytes(),
				VidPropertyConstant.ENCRYPTION_ALGORITHM.getProperty());
		byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKey, randomSeed.getBytes(), null);
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
