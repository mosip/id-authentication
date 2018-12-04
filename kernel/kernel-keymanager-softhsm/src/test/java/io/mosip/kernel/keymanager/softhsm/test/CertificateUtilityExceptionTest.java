package io.mosip.kernel.keymanager.softhsm.test;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;

import org.assertj.core.matcher.AssertionMatcher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.context.junit4.SpringRunner;

import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import io.mosip.kernel.core.keymanager.exception.KeystoreProcessingException;
import io.mosip.kernel.keymanager.softhsm.util.CertificateUtility;
@RunWith(SpringRunner.class)
public class CertificateUtilityExceptionTest {


	BouncyCastleProvider provider;
	SecureRandom random;
	KeyPairGenerator keyGenerator;
	
	@Before
	public void setUp() throws Exception  {
		
		
		provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		random = new SecureRandom();
		
	}
	
	@Test(expected=KeystoreProcessingException.class)
	public void testGenerateX509CertificateException() throws Exception {
		keyGenerator= KeyPairGenerator.getInstance("ELGAMAL", provider);
		   keyGenerator.initialize(2048, random);
	CertificateUtility.generateX509Certificate(keyGenerator.generateKeyPair(), "commonName", "organizationalUnit", "organization", "country", LocalDateTime.now(), LocalDateTime.now().minusDays(100));
	}
}
