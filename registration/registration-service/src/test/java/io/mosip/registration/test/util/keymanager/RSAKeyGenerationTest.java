package io.mosip.registration.test.util.keymanager;

import javax.crypto.Cipher;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.test.config.SpringConfiguration;
import io.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;

public class RSAKeyGenerationTest extends SpringConfiguration {
	@Autowired
	RSAKeyGenerator rsaKeyGenerator;
	@Mock
	Cipher cipher;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Test
	public void keyGenerationTest() {
		rsaKeyGenerator.generateKey();
		// Assert.fail();

	}

	@Test
	public void readPublicKeyTest() {
		Assert.assertNotNull(rsaKeyGenerator.getEncodedKey(true));
	}

	@Test
	public void readPrivateKeyTest() {
		Assert.assertNotNull(rsaKeyGenerator.getEncodedKey(true));
	}

}
