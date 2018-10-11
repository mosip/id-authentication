package io.mosip.kernel.keygenerator;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;

import javax.crypto.SecretKey;

import org.junit.Test;

public class MosipKeyGeneratorTest {

	@Test
	public void testGetSymmetricKey() {
		assertThat(MosipKeyGenerator.getSymmetricKey(), isA(SecretKey.class));
	}

	@Test
	public void testGetAsymmetricKey() {
		assertThat(MosipKeyGenerator.getAsymmetricKey(), isA(KeyPair.class));
		
	}

}
