package io.mosip.registration.test.service.packet.encryption.rsa;

import static org.mockito.Mockito.when;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

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
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.CenterMachineRepository;
import io.mosip.registration.repositories.MachineMasterRepository;
import io.mosip.registration.repositories.PolicySyncRepository;
import io.mosip.registration.service.security.impl.RSAEncryptionServiceImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContext.class })
public class RSAEncryptionServiceTest {

	@InjectMocks
	private RSAEncryptionServiceImpl rsaEncryptionServiceImpl;
	@Mock
	private PolicySyncDAO policySyncDAO;
	@Mock
	private UserOnboardDAO userOnboardDAO;
	@Mock
	private CenterMachineRepository centerMachineRepository;

	@Mock
	private MachineMasterRepository machineMasterRepository;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock@Autowired
    private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

	@Mock
	PolicySyncRepository policySyncRepository;
	
	@Before
	public void init() throws Exception {
		Map<String,Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.ASYMMETRIC_ALG_NAME, "RSA");

		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");
	}

	@Test
	public void rsaPacketCreation() throws RegBaseCheckedException {
		byte[] key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIxusCzIYkOkWjG65eeLGNSXoNghIiH1wj1lxW1ZGqr35gM4od_5MXTmRAVamgFlPko8zfFgli-h0c2yLsPbPC2IGrHLB0FQp_MaCAst2xzQvG73nAr8Fkh-geJJ0KRvZE6TCYXNdRVczHfcxctyS4PGHCrHYv6GURzDlQ5SGmXko-xA92ULxpVrD-mYlZ7uOvr92dRJGR15p-D7cNXdBWwpc812aKTwYpHd719fryXrQ4JDrdeNXsjn7Q9BlehObc_MdAn1q3glsfx_VkuYhctT-vOEHiynkKfPlSMRd041U6pGNKgoqEuyvUlTRT7SgZQgzV9m0MEhWP9peehliQIDAQAB"
				.getBytes();
		List<KeyStore> keyStoreList = new ArrayList<>();
		KeyStore keyStore = new KeyStore();
		keyStore.setPublicKey(key);
		keyStoreList.add(keyStore);
		byte[] decodedbytes = "e".getBytes();
		byte[] sessionbytes = "sesseion".getBytes();
		
		Mockito.when(userOnboardDAO.getStationID(Mockito.anyString())).thenReturn("1001");
		Mockito.when(userOnboardDAO.getCenterID(Mockito.anyString())).thenReturn("1001");
		Mockito.when(policySyncDAO.getPublicKey(Mockito.anyString())).thenReturn(keyStore);
		when(cryptoCore.asymmetricEncrypt(Mockito.any(PublicKey.class), Mockito.anyString().getBytes()))
				.thenReturn(decodedbytes);

		rsaEncryptionServiceImpl.encrypt(sessionbytes);
	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testRuntimeException() throws RegBaseCheckedException {
		when(policySyncDAO.getPublicKey(Mockito.anyString())).thenThrow(new RuntimeException("Data not found"));

		rsaEncryptionServiceImpl.encrypt("data".getBytes());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void invalidKeySpecTest() throws RegBaseCheckedException {
		byte[] key = "MIIBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgIxusCzIYkOkWjG65eeLGNSXoNghIiH1wj1lxW1ZGqr35gM4od_5MXTmRAVamgFlPko8zfFgli-h0c2yLsPbPC2IGrHLB0FQp_MaCAst2xzQvG73nAr8Fkh-geJJ0KRvZE6TCYXNdRVczHfcxctyS4PGHCrHYv6GURzDlQ5SGmXko-xA92ULxpVrD-mYlZ7uOvr92dRJGR15p-D7cNXdBWwpc812aKTwYpHd719fryXrQ4JDrdeNXsjn7Q9BlehObc_MdAn1q3glsfx_VkuYhctT-vOEHiynkKfPlSMRd041U6pGNKgoqEuyvUlTRT7SgZQgzV9m0MEhWP9peehliQIDAQAB"
				.getBytes();
		List<KeyStore> keyStoreList = new ArrayList<>();
		KeyStore keyStore = new KeyStore();
		keyStore.setPublicKey(key);
		keyStoreList.add(keyStore);
		byte[] decodedbytes = "e".getBytes();
		byte[] sessionbytes = "sesseion".getBytes();

		Mockito.when(userOnboardDAO.getStationID(Mockito.anyString())).thenReturn("1001");
		Mockito.when(userOnboardDAO.getCenterID(Mockito.anyString())).thenReturn("1001");
		Mockito.when(policySyncDAO.getPublicKey(Mockito.anyString())).thenReturn(keyStore);
		
		when(cryptoCore.asymmetricEncrypt(Mockito.any(PublicKey.class), Mockito.anyString().getBytes()))
				.thenReturn(decodedbytes);
		
		rsaEncryptionServiceImpl.encrypt(sessionbytes);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void invalidAlgorithmTest() throws Exception {
		byte[] key = "feee3c3x33r3".getBytes();
		List<KeyStore> keyStoreList = new ArrayList<>();
		KeyStore keyStore = new KeyStore();
		keyStore.setPublicKey(key);
		keyStoreList.add(keyStore);
		byte[] decodedbytes = "e".getBytes();
		byte[] sessionbytes = "sesseion".getBytes();

		Map<String,Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.ASYMMETRIC_ALG_NAME, "AES");

		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");
		when(cryptoCore.asymmetricEncrypt(Mockito.any(PublicKey.class), Mockito.anyString().getBytes()))
				.thenReturn(decodedbytes);
		Mockito.when(userOnboardDAO.getStationID(Mockito.anyString())).thenReturn("1001");
		Mockito.when(userOnboardDAO.getCenterID(Mockito.anyString())).thenReturn("1001");
		Mockito.when(policySyncDAO.getPublicKey(Mockito.anyString())).thenReturn(keyStore);

		rsaEncryptionServiceImpl.encrypt(sessionbytes);
	}

	@Test(expected = RegBaseCheckedException.class)
	public void rsaPublicKeyNotFound() throws RegBaseCheckedException {
		when(policySyncDAO.getPublicKey(Mockito.anyString())).thenReturn(null);

		rsaEncryptionServiceImpl.encrypt("data".getBytes());
	}

	@Test(expected = RegBaseCheckedException.class)
	public void invalidDataForEncryption() throws RegBaseCheckedException {
		rsaEncryptionServiceImpl.encrypt(null);
	}

}