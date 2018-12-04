package io.mosip.kernel.keymanager.softhsm.test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.Provider;
import java.security.UnrecoverableKeyException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.keymanager.exception.KeystoreProcessingException;
import io.mosip.kernel.core.keymanager.exception.NoSuchSecurityProviderException;
import io.mosip.kernel.keymanager.softhsm.impl.KeyStoreImpl;

@RunWith(PowerMockRunner.class) 
@PrepareForTest(KeyStoreImpl.class)
public class KeyStoreImplPrivateExceptionTest{

	
	private java.security.KeyStore keyStore;
	
	private KeyStoreImpl keyStoreImpl;
	
	
	@Before
	public void setUp() throws Exception  {
		keyStoreImpl=spy(KeyStoreImpl.class);
		ReflectionTestUtils.setField(keyStoreImpl, "configPath", "configPath");
		ReflectionTestUtils.setField(keyStoreImpl, "keystoreType", "keystoreType");
		ReflectionTestUtils.setField(keyStoreImpl, "keystorePass", "keystorePass");
		ReflectionTestUtils.setField(keyStoreImpl, "commonName", "commonName");
		ReflectionTestUtils.setField(keyStoreImpl, "organizationalUnit", "organizationalUnit");
		ReflectionTestUtils.setField(keyStoreImpl, "organization", "organization");
		ReflectionTestUtils.setField(keyStoreImpl, "country", "country");
	}
	@Test
	public void afterPropertiesSet() throws Exception {
		Provider provider=mock(Provider.class);
		PowerMockito.doReturn(provider).when(keyStoreImpl,"setupProvider",ArgumentMatchers.anyString());
		
	}


	

}
