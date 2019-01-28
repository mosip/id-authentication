package io.mosip.registration.test.service.packet.encryption.rsa;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.impl.RSAEncryptionServiceImpl;

import static org.mockito.Mockito.when;

public class RSAEncryptionServiceTest {

	@InjectMocks
	private RSAEncryptionServiceImpl rsaEncryptionServiceImpl;
	@Mock
	private PolicySyncDAO policySyncDAO;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;
	@Mock
	private Environment environment;

	@Test
	public void rsaPacketCreation() throws RegBaseCheckedException {
		byte[] key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIxusCzIYkOkWjG65eeLGNSXoNghIiH1wj1lxW1ZGqr35gM4od_5MXTmRAVamgFlPko8zfFgli-h0c2yLsPbPC2IGrHLB0FQp_MaCAst2xzQvG73nAr8Fkh-geJJ0KRvZE6TCYXNdRVczHfcxctyS4PGHCrHYv6GURzDlQ5SGmXko-xA92ULxpVrD-mYlZ7uOvr92dRJGR15p-D7cNXdBWwpc812aKTwYpHd719fryXrQ4JDrdeNXsjn7Q9BlehObc_MdAn1q3glsfx_VkuYhctT-vOEHiynkKfPlSMRd041U6pGNKgoqEuyvUlTRT7SgZQgzV9m0MEhWP9peehliQIDAQAB"
				.getBytes();
		KeyStore keyStore = new KeyStore();
		keyStore.setPublicKey(key);
		byte[] decodedbytes = "e".getBytes();
		byte[] sessionbytes = "sesseion".getBytes();
		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(keyStore);
		when(environment.getProperty("mosip.kernel.keygenerator.asymmetric-algorithm-name")).thenReturn("RSA");
		when(encryptor.asymmetricPublicEncrypt(Mockito.any(PublicKey.class), Mockito.anyString().getBytes()))
				.thenReturn(decodedbytes);

		rsaEncryptionServiceImpl.encrypt(sessionbytes);

	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testNullData() throws RegBaseCheckedException {
		when(policySyncDAO.findByMaxExpireTime()).thenReturn(null);
		rsaEncryptionServiceImpl.encrypt(null);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void invalidKeySpecTest() throws RegBaseCheckedException {
		byte[] key = "MIIBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIxusCzIYkOkWjG65eeLGNSXoNghIiH1wj1lxW1ZGqr35gM4od_5MXTmRAVamgFlPko8zfFgli-h0c2yLsPbPC2IGrHLB0FQp_MaCAst2xzQvG73nAr8Fkh-geJJ0KRvZE6TCYXNdRVczHfcxctyS4PGHCrHYv6GURzDlQ5SGmXko-xA92ULxpVrD-mYlZ7uOvr92dRJGR15p-D7cNXdBWwpc812aKTwYpHd719fryXrQ4JDrdeNXsjn7Q9BlehObc_MdAn1q3glsfx_VkuYhctT-vOEHiynkKfPlSMRd041U6pGNKgoqEuyvUlTRT7SgZQgzV9m0MEhWP9peehliQIDAQAB"
				.getBytes();
		KeyStore keyStore = new KeyStore();
		keyStore.setPublicKey(key);
		byte[] decodedbytes = "e".getBytes();
		byte[] sessionbytes = "sesseion".getBytes();
		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(keyStore);
		when(encryptor.asymmetricPublicEncrypt(Mockito.any(PublicKey.class), Mockito.anyString().getBytes()))
				.thenReturn(decodedbytes);
		when(environment.getProperty("mosip.kernel.keygenerator.asymmetric-algorithm-name")).thenReturn("RSA");

		rsaEncryptionServiceImpl.encrypt(sessionbytes);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void invalidAlgorithmTest() throws RegBaseCheckedException {
		byte[] key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIxusCzIYkOkWjG65eeLGNSXoNghIiH1wj1lxW1ZGqr35gM4od_5MXTmRAVamgFlPko8zfFgli-h0c2yLsPbPC2IGrHLB0FQp_MaCAst2xzQvG73nAr8Fkh-geJJ0KRvZE6TCYXNdRVczHfcxctyS4PGHCrHYv6GURzDlQ5SGmXko-xA92ULxpVrD-mYlZ7uOvr92dRJGR15p-D7cNXdBWwpc812aKTwYpHd719fryXrQ4JDrdeNXsjn7Q9BlehObc_MdAn1q3glsfx_VkuYhctT-vOEHiynkKfPlSMRd041U6pGNKgoqEuyvUlTRT7SgZQgzV9m0MEhWP9peehliQIDAQAB"
				.getBytes();
		KeyStore keyStore = new KeyStore();
		keyStore.setPublicKey(key);
		byte[] decodedbytes = "e".getBytes();
		byte[] sessionbytes = "sesseion".getBytes();
		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(keyStore);
		when(environment.getProperty("mosip.kernel.keygenerator.asymmetric-algorithm-name")).thenReturn("AES");
		when(encryptor.asymmetricPublicEncrypt(Mockito.any(PublicKey.class), Mockito.anyString().getBytes()))
				.thenReturn(decodedbytes);

		rsaEncryptionServiceImpl.encrypt(sessionbytes);

	}

}