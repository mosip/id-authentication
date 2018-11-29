package io.mosip.registration.test.service.packet.encryption.rsa;

import static io.mosip.registration.constants.RegistrationExceptions.REG_NO_SUCH_ALGORITHM_ERROR_CODE;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.impl.RSAEncryptionServiceImpl;
import io.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;

public class RSAEncryptionServiceTest {

	@InjectMocks
	private RSAEncryptionServiceImpl rsaEncryptionServiceImpl;
	@Mock
	private RSAKeyGenerator rsaKeyGenerator;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	private static KeyPair keyPair;

	@BeforeClass
	public static void generateKeyPair() {
		// Generate Key Pair
		KeyPairGenerator keyPairGenerator = null;
		try {
			// Generate key pair generator
			keyPairGenerator = KeyPairGenerator.getInstance(RegistrationConstants.RSA_ALG);
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseUncheckedException(REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage(), noSuchAlgorithmException);
		}
		// initialize key pair generator
		keyPairGenerator.initialize(2048);
		// get key pair
		keyPair = keyPairGenerator.genKeyPair();
	}

	@Test
	public void rsaPacketCreation()
			throws RegBaseCheckedException, MosipInvalidDataException, MosipInvalidKeyException {
		when(rsaKeyGenerator.getEncodedKey(true)).thenReturn(keyPair.getPublic().getEncoded());

		byte[] dataToEncrypt = "aesEncryptedInformationInBytes".getBytes();

		byte[] rsaEncryptedBytes = rsaEncryptionServiceImpl.encrypt(dataToEncrypt);
		byte[] rsaDecryptedBytes = MosipDecryptor.asymmetricPrivateDecrypt(keyPair.getPrivate().getEncoded(),
				rsaEncryptedBytes, MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
		Assert.assertArrayEquals(dataToEncrypt, rsaDecryptedBytes);

	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testNullData() throws RegBaseCheckedException {
		when(rsaKeyGenerator.getEncodedKey(true)).thenReturn(keyPair.getPublic().getEncoded());
		rsaEncryptionServiceImpl.encrypt(null);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidKey() throws RegBaseCheckedException {
		when(rsaKeyGenerator.getEncodedKey(true)).thenReturn("key".getBytes());
		rsaEncryptionServiceImpl.encrypt(null);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testInvalidData() throws RegBaseCheckedException {
		when(rsaKeyGenerator.getEncodedKey(true)).thenReturn(keyPair.getPublic().getEncoded());
		StringBuilder input = new StringBuilder();
		for (int index = 0; index < 4500; index++) {
			input.append(index);
		}
		rsaEncryptionServiceImpl.encrypt(input.toString().getBytes());
	}
}