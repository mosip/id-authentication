package io.mosip.kernel.keymanager.softhsm.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.keymanager.exception.KeystoreProcessingException;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.keymanager.softhsm.impl.KeyStoreImpl;

@RunWith(SpringRunner.class) 
public class KeyStoreImplExceptionTest{

	
	private java.security.KeyStore keyStore;
	
	private KeyStore keyStoreImpl;
	
	
	@Before
	public void setUp() throws NoSuchAlgorithmException, CertificateException, IOException {
		KeyStoreSpi keyStoreSpiMock = mock(KeyStoreSpi.class);
		keyStore = new java.security.KeyStore(keyStoreSpiMock, null, "test"){ };
		keyStoreImpl= new KeyStoreImpl(keyStore,"testkeystorepass");
		 
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
	public void testGetKey() throws Exception {
		when(keyStore.getKey(Mockito.anyString(),Mockito.any(char[].class))).thenThrow(UnrecoverableKeyException.class);
		keyStoreImpl.getKey("REGISTRATION");
	}

	/*@Test
	public void testGetAsymmetricKey() {
		when(keyStore.getKey(Mockito.anyString(),Mockito.any(char[].class))).thenThrow(UnrecoverableKeyException.class);
		keyStoreImpl.getKey("REGISTRATION");
	}*/

	/*@Test
	public void testGetPrivateKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPublicKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCertificate() {
		fail("Not yet implemented");
	}

	@Test
	public void testStoreAsymmetricKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSymmetricKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testStoreSymmetricKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteKey() {
		fail("Not yet implemented");
	}*/

}
