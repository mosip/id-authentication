package org.mosip.registration.test;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.LinkedList;

import static java.lang.System.currentTimeMillis;

import org.junit.Test;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.test.config.SpringConfiguration;
import org.mosip.registration.util.keymanager.AESKeyManager;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AESKeyManagerTest extends SpringConfiguration {

	@Autowired
	private AESKeyManager aesKeyManager;

	@Test
	public void testGenerateAESKey() throws RegBaseCheckedException {
		SecretKey sessionKey = aesKeyManager.generateSessionKey(new LinkedList<String>(
				Arrays.asList(new String[] { "D46D6D302186", "user", String.valueOf(currentTimeMillis()) })));
		assertNotNull(sessionKey);
		assertTrue(sessionKey.getEncoded().length == 32);
	}

}
