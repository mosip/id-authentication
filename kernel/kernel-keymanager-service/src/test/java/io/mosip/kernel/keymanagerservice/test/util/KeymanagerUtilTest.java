package io.mosip.kernel.keymanagerservice.test.util;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerConstant;
import io.mosip.kernel.keymanagerservice.repository.KeyAliasRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyPolicyRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyStoreRepository;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;

@SpringBootTest
@RunWith(SpringRunner.class)
public class KeymanagerUtilTest {

	@MockBean
	private KeyStore keyStore;

	@MockBean
	private KeyAliasRepository keyAliasRepository;

	@MockBean
	private KeyPolicyRepository keyPolicyRepository;

	@MockBean
	private KeyStoreRepository keyStoreRepository;

	@Autowired
	private KeymanagerUtil keymanagerUtil;

	private KeyPair keyPairMaster;

	private KeyPair keyPair;

	@Before
	public void setupKey() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KeymanagerConstant.RSA);
		keyGen.initialize(1024);
		keyPairMaster = keyGen.generateKeyPair();
		keyPair = keyGen.generateKeyPair();
	}

	@Test
	public void encryptdecryptPrivateKeyTest() {
		byte[] key = keymanagerUtil.encryptKey(keyPair.getPrivate(), keyPairMaster.getPublic());
		assertThat(key, isA(byte[].class));
		assertThat(keymanagerUtil.decryptKey(key, keyPairMaster.getPrivate()), isA(byte[].class));

	}

}
