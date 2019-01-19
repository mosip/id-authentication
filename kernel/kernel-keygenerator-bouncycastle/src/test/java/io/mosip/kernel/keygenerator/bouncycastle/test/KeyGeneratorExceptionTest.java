package io.mosip.kernel.keygenerator.bouncycastle.test;



import org.junit.Test;

import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.keygenerator.bouncycastle.util.KeyGeneratorUtils;

public class KeyGeneratorExceptionTest {

	@Test(expected = NoSuchAlgorithmException.class)
	public void testGetAsymmetricKeyException() {
       KeyGeneratorUtils.getKeyPairGenerator("AES", 204);
    }
	
	@Test(expected = NoSuchAlgorithmException.class)
	public void testGetSymmetricKeyException() {
       KeyGeneratorUtils.getKeyGenerator("RSA", 204);
    }

}
