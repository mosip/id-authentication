package io.mosip.registration.cipher;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class ClientJarEncryptionTest {
	
	@Test
	public void generateEncryptedJar() throws IOException {
		String jarPath = new ClassPathResource("src/test/resources/sample/exp.jar").getPath();
		String key = "fdHPgbFn5LZjPE8fX5S0UQ==";
		String version = "0.8.1";
		ClientJarEncryption.main(new String[] {jarPath, jarPath, key, version});
		
		assertTrue(new ClassPathResource("/sample/exp-encrypted.jar").exists());
	}
	
	@Test
	public void generateEncryptedJarForSecondPath() throws IOException {
		String jarPath = new ClassPathResource("src/test/resources/sample/exp.jar").getPath();
		String key = "fdHPgbFn5LZjPE8fX5S0UQ==";
		String version = "0.8.1";
		ClientJarEncryption.main(new String[] {null, jarPath, key, version});
		
		assertTrue(new ClassPathResource("/sample/exp-encrypted.jar").exists());
	}
}
