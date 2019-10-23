package io.mosip.kernel.keymanagerservice.util;

import static java.util.Arrays.copyOfRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.keymanager.exception.KeystoreProcessingException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanager.softhsm.constant.KeymanagerErrorCode;
import io.mosip.kernel.keymanagerservice.dto.CertificateEntry;
import io.mosip.kernel.keymanagerservice.entity.BaseEntity;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;

/**
 * Utility class for Keymanager
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */

@Component
public class KeymanagerUtil {

	private static final String UTC_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	@Value("${mosip.kernel.keygenerator.asymmetric-algorithm-name}")
	private String asymmetricAlgorithmName;
	
	/**
	 * KeySplitter for splitting key and data
	 */
	@Value("${mosip.kernel.data-key-splitter}")
	private String keySplitter;

	/**
	 * KeyGenerator instance to generate asymmetric key pairs
	 */
	@Autowired
	KeyGenerator keyGenerator;

	/**
	 * {@link CryptoCoreSpec} instance for cryptographic functionalities.
	 */
	@Autowired
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

	/**
	 * Field for symmetric Algorithm Name
	 */
	@Value("${mosip.kernel.crypto.symmetric-algorithm-name}")
	private String symmetricAlgorithmName;

	/**
	 * Function to check valid timestamp
	 * 
	 * @param timeStamp timeStamp
	 * @param keyAlias  keyAlias
	 * @return true if timestamp is valid, else false
	 */
	public boolean isValidTimestamp(LocalDateTime timeStamp, KeyAlias keyAlias) {
		return timeStamp.isEqual(keyAlias.getKeyGenerationTime()) || timeStamp.isEqual(keyAlias.getKeyExpiryTime())
				|| (timeStamp.isAfter(keyAlias.getKeyGenerationTime())
						&& timeStamp.isBefore(keyAlias.getKeyExpiryTime()));
	}

	/**
	 * Function to check if timestamp is overlapping
	 * 
	 * @param timeStamp         timeStamp
	 * @param policyExpiryTime  policyExpiryTime
	 * @param keyGenerationTime keyGenerationTime
	 * @param keyExpiryTime     keyExpiryTime
	 * @return true if timestamp is overlapping, else false
	 */
	public boolean isOverlapping(LocalDateTime timeStamp, LocalDateTime policyExpiryTime,
			LocalDateTime keyGenerationTime, LocalDateTime keyExpiryTime) {
		return !timeStamp.isAfter(keyExpiryTime) && !keyGenerationTime.isAfter(policyExpiryTime);
	}

	/**
	 * Function to check is reference id is valid
	 * 
	 * @param referenceId referenceId
	 * @return true if referenceId is valid, else false
	 */
	public boolean isValidReferenceId(String referenceId) {
		return referenceId != null && !referenceId.trim().isEmpty();
	}

	/**
	 * Function to set metadata
	 * 
	 * @param        <T> is a type parameter
	 * @param entity entity of T type
	 * @return Entity with metadata
	 */
	public <T extends BaseEntity> T setMetaData(T entity) {
		String contextUser = "SYSTEM";
		LocalDateTime time = LocalDateTime.now(ZoneId.of("UTC"));
		entity.setCreatedBy(contextUser);
		entity.setCreatedtimes(time);
		entity.setIsDeleted(false);
		return entity;
	}

	/**
	 * Function to encrypt key
	 * 
	 * @param privateKey privateKey
	 * @param masterKey  masterKey
	 * @return encrypted key
	 */
	public byte[] encryptKey(PrivateKey privateKey, PublicKey masterKey) {
		SecretKey symmetricKey = keyGenerator.getSymmetricKey();
		byte[] encryptedPrivateKey = cryptoCore.symmetricEncrypt(symmetricKey, privateKey.getEncoded(),null);
		byte[] encryptedSymmetricKey = cryptoCore.asymmetricEncrypt(masterKey, symmetricKey.getEncoded());
		return CryptoUtil.combineByteArray(encryptedPrivateKey, encryptedSymmetricKey, keySplitter);
	}

	/**
	 * Function to decrypt key
	 * 
	 * @param key        key
	 * @param privateKey privateKey
	 * @return decrypted key
	 */
	public byte[] decryptKey(byte[] key, PrivateKey privateKey) {
		
		
		int keyDemiliterIndex = 0;
		final int cipherKeyandDataLength = key.length;
		final int keySplitterLength = keySplitter.length();
		keyDemiliterIndex = CryptoUtil.getSplitterIndex(key, keyDemiliterIndex, keySplitter);
		byte[] encryptedKey = copyOfRange(key, 0, keyDemiliterIndex);
		byte[] encryptedData = copyOfRange(key, keyDemiliterIndex + keySplitterLength, cipherKeyandDataLength);
		byte[] decryptedSymmetricKey = cryptoCore.asymmetricDecrypt(privateKey, encryptedKey);
		SecretKey symmetricKey = new SecretKeySpec(decryptedSymmetricKey, 0, decryptedSymmetricKey.length,
				symmetricAlgorithmName);
		return cryptoCore.symmetricDecrypt(symmetricKey, encryptedData,null);
	}

	/**
	 * Parse a date string of pattern UTC_DATETIME_PATTERN into
	 * {@link LocalDateTime}
	 * 
	 * @param dateTime of type {@link String} of pattern UTC_DATETIME_PATTERN
	 * @return a {@link LocalDateTime} of given pattern
	 */
	public LocalDateTime parseToLocalDateTime(String dateTime) {
		return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN));
	}
	
	
	public void isCertificateValid(CertificateEntry<X509Certificate, PrivateKey> certificateEntry,Date inputDate) {
		try {
			certificateEntry.getChain()[0].checkValidity(inputDate);
		} catch (CertificateExpiredException | CertificateNotYetValidException e) {
			throw new KeystoreProcessingException(KeymanagerErrorCode.CERTIFICATE_PROCESSING_ERROR.getErrorCode(),
					KeymanagerErrorCode.CERTIFICATE_PROCESSING_ERROR.getErrorMessage() + e.getMessage());
		}
	}
	
	public PrivateKey privateKeyExtractor(InputStream privateKeyInputStream) {

		KeyFactory kf = null;
		PKCS8EncodedKeySpec keySpec = null;
		PrivateKey privateKey = null;
		try {
			StringWriter stringWriter= new StringWriter();
			IOUtils.copy(privateKeyInputStream, stringWriter, StandardCharsets.UTF_8);
			String privateKeyPEMString= stringWriter.toString(); 
			byte[] decodedKey = Base64.decodeBase64(privateKeyPEMString);
			kf = KeyFactory.getInstance(asymmetricAlgorithmName);
			keySpec = new PKCS8EncodedKeySpec(decodedKey);
			privateKey = kf.generatePrivate(keySpec);

		} catch ( NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			throw new KeystoreProcessingException(KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorCode(),
					KeymanagerErrorCode.KEYSTORE_PROCESSING_ERROR.getErrorMessage() + e.getMessage());
		}

		return privateKey;
	}

}
