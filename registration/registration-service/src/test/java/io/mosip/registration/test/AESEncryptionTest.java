package io.mosip.registration.test;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Assert;
import org.junit.Test;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.registration.test.config.SpringConfiguration;
import io.mosip.registration.test.util.aes.AESDecryption;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.encryption.aes.AESSeedGenerator;
import io.mosip.registration.util.keymanager.AESKeyManager;

public class AESEncryptionTest extends SpringConfiguration {

	@Autowired
	private AESDecryption aesDecryption;
	@Autowired
	private AESKeyManager aesKeyManager;
	@Autowired
	private AESSeedGenerator aesSeedGenerator;

	@Test
	public void aesEncryptionTest() throws RegBaseCheckedException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, MosipInvalidDataException, MosipInvalidKeyException {
		// Enable AES 256 bit encryption
		Security.setProperty("crypto.policy", "unlimited");

		List<String> aesKeySeeds = aesSeedGenerator.generateAESKeySeeds();
		byte[] sessionKey = aesKeyManager.generateSessionKey(aesKeySeeds).getEncoded();
		byte[] plainData = "Welcome To AES".getBytes();
		byte[] encryptedData = MosipEncryptor.symmetricEncrypt(sessionKey, plainData, MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING); 
				//aesEncryption.encrypt(plainData, sessionKey);
		byte[] decryptedData = aesDecryption.decrypt(encryptedData, sessionKey);

		Assert.assertArrayEquals(plainData, decryptedData);
	}

}
