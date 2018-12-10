package io.mosip.kernel.keymanagerservice.util;

import static java.util.Arrays.copyOfRange;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanagerservice.entity.BaseEntity;

/**
 * Utility class for Keymanager
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class KeymanagerUtil {

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
	 * Decryptor instance to decrypt data
	 */
	@Autowired
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	/**
	 * Encryptor instance to decrypt data
	 */
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	/**
	 * Field for symmetric Algorithm Name
	 */
	@Value("${mosip.kernel.keygenerator.symmetric-algorithm-name}")
	private String symmetricAlgorithmName;

	/**
	 * Function to set metadata
	 * 
	 * @param entity
	 *            entity
	 * @return Entity with metadata
	 */
	public <T extends BaseEntity> T setMetaData(T entity) {
		String contextUser = "defaultadmin@mosip.io";
		LocalDateTime time = LocalDateTime.now(ZoneId.of("UTC"));
		entity.setCreatedBy(contextUser);
		entity.setCreatedtimes(time);
		entity.setIsDeleted(false);
		return entity;
	}

	/**
	 * Function to encrypt key
	 * 
	 * @param privateKey
	 *            privateKey
	 * @param masterKey
	 *            masterKey
	 * @return encrypted key
	 */
	public byte[] encryptKey(PrivateKey privateKey, PublicKey masterKey) {
		SecretKey symmetricKey = keyGenerator.getSymmetricKey();
		byte[] encryptedPrivateKey = encryptor.symmetricEncrypt(symmetricKey, privateKey.getEncoded());
		byte[] encryptedSymmetricKey = encryptor.asymmetricPublicEncrypt(masterKey, symmetricKey.getEncoded());
		return CryptoUtil.combineByteArray(encryptedPrivateKey, encryptedSymmetricKey, keySplitter);
	}

	/**
	 * Function to decrypt key
	 * 
	 * @param key
	 *            key
	 * @param privateKey
	 *            privateKey
	 * @return decrypted key
	 */
	public byte[] decryptKey(byte[] key, PrivateKey privateKey) {
		int keyDemiliterIndex = 0;
		final int cipherKeyandDataLength = key.length;
		final int keySplitterLength = keySplitter.length();
		keyDemiliterIndex = CryptoUtil.getSplitterIndex(key, keyDemiliterIndex, keySplitterLength, keySplitter);
		byte[] encryptedKey = copyOfRange(key, 0, keyDemiliterIndex);
		byte[] encryptedData = copyOfRange(key, keyDemiliterIndex + keySplitterLength, cipherKeyandDataLength);
		byte[] decryptedSymmetricKey = decryptor.asymmetricPrivateDecrypt(privateKey, encryptedKey);
		SecretKey symmetricKey = new SecretKeySpec(decryptedSymmetricKey, 0, decryptedSymmetricKey.length,
				symmetricAlgorithmName);
		return decryptor.symmetricDecrypt(symmetricKey, encryptedData);
	}

}
