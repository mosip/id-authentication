package io.mosip.kernel.keymanager.softhsm.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreSpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.keymanager.softhsm.impl.KeyStoreImpl;
import io.mosip.kernel.keymanager.softhsm.util.CertificateUtility;

@RunWith(SpringRunner.class) 

public class KeyStoreImplTest{

	
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
	
	
   @Test
	public void testStoreAsymmetricKey() throws Exception{
	   
		
	  KeyPairGenerator keyGenerator= KeyPairGenerator.getInstance("RSA", provider);
	   keyGenerator.initialize(2048, random);
	 
		keyStoreImpl.storeAsymmetricKey(keyGenerator.generateKeyPair(), "alias", LocalDateTime.now(), LocalDateTime.now().plusDays(100));
		  
	}

   @Test
	public void testStoreSymmetricKey() throws Exception{
	   
		
	   javax.crypto.KeyGenerator keyGenerator=  javax.crypto.KeyGenerator.getInstance("AES",provider);
	   keyGenerator.init(256, random);
	 
		keyStoreImpl.storeSymmetricKey(keyGenerator.generateKey(), "alias");
		  
	}
   
   @Test
	public void testDeleteKey() throws Exception {
	  keyStoreImpl.deleteKey("alias");
	}
	@Test
	public void testGetPrivateKey() throws Exception {
		KeyPairGenerator keyGenerator= KeyPairGenerator.getInstance("RSA", provider);
		   keyGenerator.initialize(2048, random);
		 KeyPair keyPair=keyGenerator.generateKeyPair();
		X509Certificate[] chain = new X509Certificate[1];
		chain[0] = CertificateUtility.generateX509Certificate(keyPair, "commonName", "organizationalUnit", "organization",
				"country", LocalDateTime.now(), LocalDateTime.now().plusDays(100));
		 PrivateKeyEntry keyEntry=new PrivateKeyEntry(keyPair.getPrivate(), chain);
		when(keyStore.entryInstanceOf("alias", PrivateKeyEntry.class)).thenReturn(true);
		  when(keyStore.getEntry(Mockito.anyString(), Mockito.any())).thenReturn(keyEntry);
		  assertThat(keyStoreImpl.getPrivateKey("alias"),isA(PrivateKey.class));
	}

	@Test
	public void testGetPublicKey() throws Exception {
		KeyPairGenerator keyGenerator= KeyPairGenerator.getInstance("RSA", provider);
		   keyGenerator.initialize(2048, random);
		 KeyPair keyPair=keyGenerator.generateKeyPair();
		X509Certificate[] chain = new X509Certificate[1];
		chain[0] = CertificateUtility.generateX509Certificate(keyPair, "commonName", "organizationalUnit", "organization",
				"country", LocalDateTime.now(), LocalDateTime.now().plusDays(100));
		 PrivateKeyEntry keyEntry=new PrivateKeyEntry(keyPair.getPrivate(), chain);
			when(keyStore.entryInstanceOf("alias", PrivateKeyEntry.class)).thenReturn(true);
			  when(keyStore.getEntry(Mockito.anyString(), Mockito.any())).thenReturn(keyEntry);
			  assertThat(keyStoreImpl.getPublicKey("alias"),isA(PublicKey.class));
	}

	@Test
	public void testGetCertificate() throws Exception {
		KeyPairGenerator keyGenerator= KeyPairGenerator.getInstance("RSA", provider);
		   keyGenerator.initialize(2048, random);
		 KeyPair keyPair=keyGenerator.generateKeyPair();
		X509Certificate[] chain = new X509Certificate[1];
		chain[0] = CertificateUtility.generateX509Certificate(keyPair, "commonName", "organizationalUnit", "organization",
				"country", LocalDateTime.now(), LocalDateTime.now().plusDays(100));
		 PrivateKeyEntry keyEntry=new PrivateKeyEntry(keyPair.getPrivate(), chain);
			when(keyStore.entryInstanceOf("alias", PrivateKeyEntry.class)).thenReturn(true);
			  when(keyStore.getEntry(Mockito.anyString(), Mockito.any())).thenReturn(keyEntry);
			  assertThat(keyStoreImpl.getCertificate("alias"),isA(X509Certificate.class));
	}
	
	@Test
	public void testGetAllAlias() throws Exception {
		Enumeration<String> enumeration= mock(Enumeration.class);
	    when(keyStore.aliases()).thenReturn(enumeration);
		assertThat(keyStoreImpl.getAllAlias(),isA(List.class));;
	}
	
	@Test
	public void testGetKey() throws Exception {
		Key key=mock(Key.class);
	    when(keyStore.getKey(Mockito.anyString(),Mockito.any())).thenReturn(key);
		assertThat(keyStoreImpl.getKey("alias"),isA(Key.class));;
	}
	
	
	@Test
	public void testGetSymmetricKey() throws Exception {
		  javax.crypto.KeyGenerator keyGenerator=  javax.crypto.KeyGenerator.getInstance("AES",provider);
		   keyGenerator.init(256, random);
		SecretKeyEntry secretKeyEntry= new SecretKeyEntry(keyGenerator.generateKey());
	    when(keyStore.entryInstanceOf("alias", SecretKeyEntry.class)).thenReturn(true);
	    when(keyStore.getEntry(Mockito.anyString(), Mockito.any())).thenReturn(secretKeyEntry);
		assertThat(keyStoreImpl.getSymmetricKey("alias"),isA(Key.class));
	}

}
