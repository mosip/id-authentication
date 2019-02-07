package io.mosip.registration.cipher;

import org.junit.Test;

public class ClientJarEncryptionTest {
	
	@Test
	public void generateEncryptedJar() {
		String args[] = new String[]{"exp.jar", null, "fdHPgbFn5LZjPE8fX5S0UQ==", "0.8.1"};
		ClientJarEncryption.main(args);
	}

}
