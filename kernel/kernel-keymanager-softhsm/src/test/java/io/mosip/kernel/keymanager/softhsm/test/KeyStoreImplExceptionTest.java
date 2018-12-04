package io.mosip.kernel.keymanager.softhsm.test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.UnrecoverableKeyException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.keymanager.exception.KeystoreProcessingException;
import io.mosip.kernel.core.keymanager.exception.NoSuchSecurityProviderException;
import io.mosip.kernel.keymanager.softhsm.impl.KeyStoreImpl;

@RunWith(SpringRunner.class) 

public class KeyStoreImplExceptionTest{

	
	private java.security.KeyStore keyStore;
	
	private KeyStoreImpl keyStoreImpl;
	
	
	@Before
	public void setUp() throws Exception  {
		
		KeyStoreSpi keyStoreSpiMock = mock(KeyStoreSpi.class);
		keyStore = new java.security.KeyStore(keyStoreSpiMock, null, "test"){ };
		
		keyStoreImpl = new KeyStoreImpl();
		keyStoreImpl.setKeyStore(keyStore);
		ReflectionTestUtils.setField(keyStoreImpl, "configPath", "configPath");
		ReflectionTestUtils.setField(keyStoreImpl, "keystoreType", "keystoreType");
		ReflectionTestUtils.setField(keyStoreImpl, "keystorePass", "keystorePass");
		ReflectionTestUtils.setField(keyStoreImpl, "commonName", "commonName");
		ReflectionTestUtils.setField(keyStoreImpl, "organizationalUnit", "organizationalUnit");
		ReflectionTestUtils.setField(keyStoreImpl, "organization", "organization");
		ReflectionTestUtils.setField(keyStoreImpl, "country", "country");
		keyStore.load(null);
	}
	/*@Test
	public void testKeyStoreImpl() throws Exception {
		
	}*/

	@Test(expected=KeystoreProcessingException.class)
	public void testGetAllAliasKeystoreProcessingException() throws Exception  {
		when(keyStore.aliases()).thenThrow(KeyStoreException.class);
		keyStoreImpl.getAllAlias();
	}

	@Test(expected=KeystoreProcessingException.class) 
	public void testGetKeyKeystoreProcessingException() throws Exception {
		when(keyStore.getKey(Mockito.anyString(),Mockito.any(char[].class))).thenThrow(UnrecoverableKeyException.class);
		keyStoreImpl.getKey("REGISTRATION");
	}

	@Test(expected=NoSuchSecurityProviderException.class)
	public void testGetAsymmetricKeyNoSuchSecurityProviderException() throws Exception {
		when(keyStore.entryInstanceOf("alias", PrivateKeyEntry.class)).thenReturn(false);
		keyStoreImpl.getAsymmetricKey("alias");
	}
	
	@Test(expected=KeystoreProcessingException.class)
	public void testGetAsymmetricKeyKeystoreProcessingException() throws Exception {
		when(keyStore.entryInstanceOf("alias", PrivateKeyEntry.class)).thenReturn(true);
		when(keyStore.getEntry(Mockito.anyString(), Mockito.any())).thenThrow(KeyStoreException.class);
		keyStoreImpl.getAsymmetricKey("alias");
	}

	
  

	@Test(expected=NoSuchSecurityProviderException.class)
	public void testGetSymmetricKeyNoSuchSecurityProviderException() throws Exception {
		when(keyStore.entryInstanceOf("alias", PrivateKeyEntry.class)).thenReturn(false);
		keyStoreImpl.getSymmetricKey("alias");
	}
	
	@Test(expected=KeystoreProcessingException.class)
	public void testGetSymmetricKeyKeystoreProcessingException() throws Exception {
		when(keyStore.entryInstanceOf("alias", SecretKeyEntry.class)).thenReturn(true);
		when(keyStore.getEntry(Mockito.anyString(), Mockito.any())).thenThrow(KeyStoreException.class);
		keyStoreImpl.getSymmetricKey("alias");
	}

	/*@Test
	public void testStoreSymmetricKey() {
		fail("Not yet implemented");
	}
*/
	

}
