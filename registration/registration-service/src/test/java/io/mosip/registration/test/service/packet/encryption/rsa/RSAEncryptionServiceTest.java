package io.mosip.registration.test.service.packet.encryption.rsa;

import static org.mockito.Mockito.when;

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

import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.impl.RSAEncryptionServiceImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MosipEncryptor.class, CryptoUtil.class })
public class RSAEncryptionServiceTest {

	@InjectMocks
	private RSAEncryptionServiceImpl rsaEncryptionServiceImpl;
	@Mock
	private PolicySyncDAO policySyncDAO;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	//@Test
	public void rsaPacketCreation()
			throws RegBaseCheckedException, MosipInvalidDataException, MosipInvalidKeyException {
		PowerMockito.mockStatic(MosipEncryptor.class);
		PowerMockito.mockStatic(CryptoUtil.class);
		KeyStore keyStore = new KeyStore();
		keyStore.setPublicKey("test".getBytes());
		byte[] key = "test".getBytes();
		byte[] decodedbytes = "e".getBytes();
		byte[] sessionbytes = "sesseion".getBytes();
		Mockito.when(policySyncDAO.findByMaxExpireTime()).thenReturn(keyStore);
		Mockito.when(CryptoUtil.decodeBase64(Mockito.anyString())).thenReturn(key);
		Mockito.when(MosipEncryptor.asymmetricPublicEncrypt(Mockito.anyString().getBytes(),
				Mockito.anyString().getBytes(), Mockito.any(MosipSecurityMethod.class))).thenReturn(decodedbytes);
		rsaEncryptionServiceImpl.encrypt(sessionbytes);

	}

	@Test(expected = RegBaseUncheckedException.class)
	public void testNullData() throws RegBaseCheckedException {
		when(policySyncDAO.findByMaxExpireTime()).thenReturn(null);
		rsaEncryptionServiceImpl.encrypt(null);
	}
	

}