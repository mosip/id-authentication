package io.mosip.registration.test.util.keymanager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.util.rsa.keygenerator.impl.RSAKeyGeneratorImpl;

public class RSAKeyGenerationTest {
	@InjectMocks
	private RSAKeyGeneratorImpl rsaKeyGeneratorImpl;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Before
	public void keyGenerationTest() {
		rsaKeyGeneratorImpl.generateKey();
	}

	@Test
	public void readPublicKeyTest() {
		Assert.assertNotNull(rsaKeyGeneratorImpl.getEncodedKey(true));
	}

	@Test
	public void readPrivateKeyTest() {
		Assert.assertNotNull(rsaKeyGeneratorImpl.getEncodedKey(false));
	}

}
