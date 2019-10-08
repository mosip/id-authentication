package io.mosip.registration.test.service.packet.encryption.aes;

import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

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

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.security.RSAEncryptionService;
import io.mosip.registration.service.security.impl.AESEncryptionServiceImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContext.class })
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
	private AuditManagerService auditFactory;
	@Mock
    private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;


	private String keySplitter = "#Key_Splitter#";

	@Before
	public void initialize() throws Exception {
		Map<String,Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.KEY_SPLITTER, keySplitter);

		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");
	}

	@Test
	public void aesEncryptionTest() throws RegBaseCheckedException, NoSuchAlgorithmException {
		SecretKey sessionKey = new SecretKeySpec(new byte[] { -126, -104, 114, 70, 3, 89, -68, -84, 77, 20, 39, -13,
				-75, 99, 113, -11, 101, 10, 41, 14, 86, 6, 11, 98, 37, 5, -70, -1, -5, -73, -20, -50 }, "AES");

		when(keyGenerator.getSymmetricKey()).thenReturn(sessionKey);
		when(rsaEncryptionService.encrypt(Mockito.anyString().getBytes())).thenReturn("rsa".getBytes());
		when(cryptoCore.symmetricEncrypt(Mockito.any(SecretKey.class), Mockito.anyString().getBytes(),Mockito.eq(null)))
				.thenReturn("encrypted".getBytes());

		byte[] dataToEncrypt = "original data".getBytes();
		byte[] encryptedData = aesEncryptionServiceImpl.encrypt(dataToEncrypt);
		String ciperText = new String(encryptedData);
		Assert.assertTrue(ciperText.contains("rsa" + keySplitter));
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void aesEncryptionRuntimeExpTest() throws RegBaseCheckedException {
		when(rsaEncryptionService.encrypt(Mockito.anyString().getBytes()))
				.thenThrow(new RuntimeException("Invalid data"));
		aesEncryptionServiceImpl.encrypt("data".getBytes());
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseCheckedException.class)
	public void invalidKeyExpTest() throws RegBaseCheckedException, NoSuchAlgorithmException {
		SecretKey sessionKey = new SecretKeySpec(new byte[] {22}, "AES");
		when(this.keyGenerator.getSymmetricKey()).thenReturn(sessionKey);
		when(cryptoCore.symmetricEncrypt(Mockito.any(SecretKey.class), Mockito.anyString().getBytes(),Mockito.eq(null)))
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
		when(cryptoCore.symmetricEncrypt(Mockito.any(SecretKey.class), Mockito.anyString().getBytes(), Mockito.eq(null)))
		.thenThrow(MosipInvalidDataException.class);

		aesEncryptionServiceImpl.encrypt("dataToEncrypt".getBytes());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void keySplitterParamNotFound() throws Exception {
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(new HashMap<>()).when(ApplicationContext.class, "map");

		aesEncryptionServiceImpl.encrypt(null);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void keySplitterParamInvalid() throws Exception {
		PowerMockito.mockStatic(ApplicationContext.class);
		Map<String, Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.KEY_SPLITTER, RegistrationConstants.EMPTY);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");

		aesEncryptionServiceImpl.encrypt(null);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void dataToBeEncryptedInvalid() throws Exception {
		aesEncryptionServiceImpl.encrypt(null);
	}

}
