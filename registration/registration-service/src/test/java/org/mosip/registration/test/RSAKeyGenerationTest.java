package org.mosip.registration.test;

import javax.crypto.Cipher;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mosip.registration.config.SpringConfiguration;
import org.mosip.registration.consts.RegConstants;
import org.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;

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
		Assert.assertNotNull(rsaKeyGenerator.readPublickey(RegConstants.RSA_PUBLIC_KEY_FILE));
	}

	@Test
	public void readPrivateKeyTest() {
		Assert.assertNotNull(rsaKeyGenerator.readPublickey(RegConstants.RSA_PRIVATE_KEY_FILE));
	}

}
