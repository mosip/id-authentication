package io.mosip.kernel.keygenerator.bouncycastle.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;

import javax.crypto.SecretKey;

import org.junit.Test;

import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

public class KeyGeneratorTest {

	@Test
	public void testGetSymmetricKey() {
		assertThat(KeyGenerator.getSymmetricKey(), isA(SecretKey.class));
	}

	@Test
	public void testGetAsymmetricKey() {
		assertThat(KeyGenerator.getAsymmetricKey(), isA(KeyPair.class));
		
	}

}
