package io.mosip.registration.test.util.keymanager;

import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedList;

import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.keymanager.impl.AESKeyManagerImpl;

public class AESKeyManagerTest  {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private AESKeyManagerImpl aesKeyManagerImpl;
	@Mock
	private Environment environment;
	
	@Before
	public void initialize() {
		when(environment.getProperty(RegistrationConstants.AES_KEY_MANAGER_ALG)).thenReturn("AES");
		when(environment.getProperty(RegistrationConstants.AES_KEY_SEED_LENGTH)).thenReturn("32");
		when(environment.getProperty(RegistrationConstants.AES_SESSION_KEY_LENGTH)).thenReturn("256");
	}

	@Test
	public void testGenerateAESKey() throws RegBaseCheckedException {
		SecretKey sessionKey = aesKeyManagerImpl.generateSessionKey(new LinkedList<String>(
				Arrays.asList(new String[] { "D46D6D302186", "user", String.valueOf(currentTimeMillis()) })));
		assertNotNull(sessionKey);
		assertTrue(sessionKey.getEncoded().length == 32);
		
		sessionKey = aesKeyManagerImpl.generateSessionKey(new LinkedList<String>(
				Arrays.asList(new String[] { "D46D6D302186", "Balaji Sridharan", String.valueOf(currentTimeMillis()) })));
		assertNotNull(sessionKey);
		assertTrue(sessionKey.getEncoded().length == 32);
	}
	
	@Test(expected = RegBaseUncheckedException.class)
	public void testUncheckedException() throws RegBaseCheckedException {
		aesKeyManagerImpl.generateSessionKey(null);
	}

}
