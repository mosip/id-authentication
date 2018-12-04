package io.mosip.kernel.keymanager.softhsm.test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.time.LocalDateTime;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
	
	BouncyCastleProvider provider;
	SecureRandom random;
	
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
		provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		random = new SecureRandom();
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

	
	@Test(expected=NoSuchSecurityProviderException.class)
	public void testAfterPropertiesSet() throws Exception {
	    keyStoreImpl.afterPropertiesSet();
	}
	
	@Test(expected=KeystoreProcessingException.class)
	public void testDeleteKeyKeystoreProcessingException() throws Exception {
	    keyStore=mock(KeyStore.class);
	    keyStoreImpl.setKeyStore(keyStore);
		keyStoreImpl.deleteKey("alias");
	}
	
	@Test(expected=KeystoreProcessingException.class)
	public void testStoreSymmetricKeyKeystoreProcessingException() throws Exception {
	    keyStore=mock(KeyStore.class);
	    keyStoreImpl.setKeyStore(keyStore);
	    javax.crypto.KeyGenerator keyGenerator=  javax.crypto.KeyGenerator.getInstance("AES",provider);
		   keyGenerator.init(256, random);
		keyStoreImpl.storeSymmetricKey(keyGenerator.generateKey(), "alias");
	}
	
	@Test(expected=KeystoreProcessingException.class)
	public void testStoreAsymmetricKeyKeystoreProcessingException() throws Exception {
	    keyStore=mock(KeyStore.class);
	    keyStoreImpl.setKeyStore(keyStore);
	    KeyPairGenerator keyGenerator= KeyPairGenerator.getInstance("RSA", provider);
		   keyGenerator.initialize(2048, random);
		 KeyPair keyPair=keyGenerator.generateKeyPair();
		keyStoreImpl.storeAsymmetricKey(keyPair, "alias", LocalDateTime.now(), LocalDateTime.now().plusDays(365));
	}
	

	

}
