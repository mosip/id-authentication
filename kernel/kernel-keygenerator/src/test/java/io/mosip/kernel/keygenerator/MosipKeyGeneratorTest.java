package io.mosip.kernel.keygenerator;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import io.mosip.kernel.keygenerator.asymmetrickeypair.MosipAsymmetricKeyPair;
public class MosipKeyGeneratorTest {

	@Test
	public void testGetSymmetricKey() {
		assertThat(MosipKeyGenerator.getSymmetricKey(), isA(byte[].class));
	}

	@Test
	public void testGetAsymmetricKey() {
		assertThat(MosipKeyGenerator.getAsymmetricKey(), isA(MosipAsymmetricKeyPair.class));
		MosipAsymmetricKeyPair pair = MosipKeyGenerator.getAsymmetricKey();
		assertThat(pair.getPrivateKey(), isA(byte[].class));
		assertThat(pair.getPublicKey(), isA(byte[].class));
	}

}
