package io.mosip.registration.test.service.packet.encryption.aes;

import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.security.constants.MosipSecurityExceptionCodeConstants;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.RSAEncryptionService;
import io.mosip.registration.service.impl.AESEncryptionServiceImpl;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MosipEncryptor.class })
public class AESEncryptionServiceTest {

	@InjectMocks
	private AESEncryptionServiceImpl aesEncryptionServiceImpl;
	@Mock
	private KeyGenerator keyGenerator;
	@Mock
	private RSAEncryptionService rsaEncryptionManager;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private AuditFactory auditFactory;
	@Mock
	private Environment environment;

	private String keySplitter = "#Key_Splitter#";

	@Before
	public void initialize() throws RegBaseCheckedException {

		ReflectionTestUtils.setField(aesEncryptionServiceImpl, "environment", environment);

		when(environment.getProperty(RegistrationConstants.AES_KEY_MANAGER_ALG)).thenReturn("AES");
		when(environment.getProperty(RegistrationConstants.AES_KEY_SEED_LENGTH)).thenReturn("32");
		when(environment.getProperty(RegistrationConstants.AES_SESSION_KEY_LENGTH)).thenReturn("256");
		when(environment.getProperty(RegistrationConstants.AES_KEY_CIPHER_SPLITTER)).thenReturn(keySplitter);
	}

	//@Test
	public void aesEncryptionTest() throws RegBaseCheckedException, NoSuchAlgorithmException {
		SecretKey sessionKey = new SecretKeySpec(new byte[] { -126, -104, 114, 70, 3, 89, -68, -84, 77, 20, 39, -13,
				-75, 99, 113, -11, 101, 10, 41, 14, 86, 6, 11, 98, 37, 5, -70, -1, -5, -73, -20, -50 }, "AES");
		
		when(keyGenerator.getSymmetricKey()).thenReturn(sessionKey);
		when(rsaEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		PowerMockito.mockStatic(MosipEncryptor.class);
		PowerMockito.when(MosipEncryptor.symmetricEncrypt(Mockito.anyString().getBytes(),
				Mockito.anyString().getBytes(), Mockito.any(MosipSecurityMethod.class)))
				.thenReturn("encrypted".getBytes());

		byte[] dataToEncrypt = "original data".getBytes();
		byte[] encryptedData = aesEncryptionServiceImpl.encrypt(dataToEncrypt);
		String ciperText = new String(encryptedData);
		Assert.assertTrue(ciperText.contains("rsa" + keySplitter));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void aesEncryptionRuntimeExpTest() throws RegBaseCheckedException {
		when(rsaEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		aesEncryptionServiceImpl.encrypt(null);
	}

	//@Test(expected = RegBaseCheckedException.class)
	public void invalidKeyExpTest() throws RegBaseCheckedException, NoSuchAlgorithmException {
		SecretKey sessionKey = new SecretKeySpec(new byte[] {22}, "AES");
		when(this.keyGenerator.getSymmetricKey()).thenReturn(sessionKey);
		when(rsaEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		aesEncryptionServiceImpl.encrypt("encrypt".getBytes());
	}

	//@Test(expected = RegBaseCheckedException.class)
	public void invalidDataExpTest() throws RegBaseCheckedException, NoSuchAlgorithmException {
		SecretKey sessionKey = new SecretKeySpec(new byte[] { -126, -104, 114, 70, 3, 89, -68, -84, 77, 20, 39, -13,
				-75, 99, 113, -11, 101, 10, 41, 14, 86, 6, 11, 98, 37, 5, -70, -1, -5, -73, -20, -50 }, "AES");
		
		when(keyGenerator.getSymmetricKey()).thenReturn(sessionKey);
		when(rsaEncryptionManager.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		PowerMockito.mockStatic(MosipEncryptor.class);
		PowerMockito.when(MosipEncryptor.symmetricEncrypt(Mockito.anyString().getBytes(),
				Mockito.anyString().getBytes(), Mockito.any(MosipSecurityMethod.class)))
				.thenThrow(new MosipInvalidDataException(MosipSecurityExceptionCodeConstants.MOSIP_INVALID_DATA_EXCEPTION));

		byte[] dataToEncrypt = "original data".getBytes();
		byte[] encryptedData = aesEncryptionServiceImpl.encrypt(dataToEncrypt);
		String ciperText = new String(encryptedData);
		Assert.assertTrue(ciperText.contains("rsa" + keySplitter));
	}

}
