package io.mosip.registration.test.service.packet.encryption.aes;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.RSAEncryptionService;
import io.mosip.registration.service.impl.AESEncryptionServiceImpl;

import static org.mockito.Mockito.when;

public class AESEncryptionServiceTest {

	@InjectMocks
	private AESEncryptionServiceImpl aesEncryptionServiceImpl;
	@Mock
	private KeyGenerator keyGenerator;
	@Mock
	private RSAEncryptionService rsaEncryptionService;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private AuditFactory auditFactory;
	@Mock
	private Environment environment;
	@Mock
	private Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	private String keySplitter = "#Key_Splitter#";

	@Before
	public void initialize() throws RegBaseCheckedException {
		when(environment.getProperty(RegistrationConstants.AES_KEY_CIPHER_SPLITTER)).thenReturn(keySplitter);
	}

	@Test
	public void aesEncryptionTest() throws RegBaseCheckedException, NoSuchAlgorithmException {
		SecretKey sessionKey = new SecretKeySpec(new byte[] { -126, -104, 114, 70, 3, 89, -68, -84, 77, 20, 39, -13,
				-75, 99, 113, -11, 101, 10, 41, 14, 86, 6, 11, 98, 37, 5, -70, -1, -5, -73, -20, -50 }, "AES");

		when(keyGenerator.getSymmetricKey()).thenReturn(sessionKey);
		when(rsaEncryptionService.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		when(encryptor.symmetricEncrypt(Mockito.any(SecretKey.class), Mockito.anyString().getBytes()))
				.thenReturn("encrypted".getBytes());

		byte[] dataToEncrypt = "original data".getBytes();
		byte[] encryptedData = aesEncryptionServiceImpl.encrypt(dataToEncrypt);
		String ciperText = new String(encryptedData);
		Assert.assertTrue(ciperText.contains("rsa" + keySplitter));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void aesEncryptionRuntimeExpTest() throws RegBaseCheckedException {
		when(rsaEncryptionService.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		aesEncryptionServiceImpl.encrypt(null);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void invalidKeyExpTest() throws RegBaseCheckedException, NoSuchAlgorithmException {
		SecretKey sessionKey = new SecretKeySpec(new byte[] {22}, "AES");
		when(this.keyGenerator.getSymmetricKey()).thenReturn(sessionKey);
		when(encryptor.symmetricEncrypt(Mockito.any(SecretKey.class), Mockito.anyString().getBytes()))
		.thenThrow(MosipInvalidKeyException.class);
		when(rsaEncryptionService.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());

		aesEncryptionServiceImpl.encrypt("encrypt".getBytes());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void invalidDataExpTest() throws RegBaseCheckedException, NoSuchAlgorithmException {
		SecretKey sessionKey = new SecretKeySpec(new byte[] { -126, -104, 114, 70, 3, 89, -68, -84, 77, 20, 39, -13,
				-75, 99, 113, -11, 101, 10, 41, 14, 86, 6, 11, 98, 37, 5, -70, -1, -5, -73, -20, -50 }, "AES");

		when(keyGenerator.getSymmetricKey()).thenReturn(sessionKey);
		when(rsaEncryptionService.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		when(encryptor.symmetricEncrypt(Mockito.any(SecretKey.class), Mockito.anyString().getBytes()))
		.thenThrow(MosipInvalidDataException.class);

		aesEncryptionServiceImpl.encrypt("dataToEncrypt".getBytes());
	}

}
