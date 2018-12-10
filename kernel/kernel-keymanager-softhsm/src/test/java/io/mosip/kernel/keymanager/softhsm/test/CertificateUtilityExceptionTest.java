package io.mosip.kernel.keymanager.softhsm.test;

import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.time.LocalDateTime;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.keymanager.exception.KeystoreProcessingException;
import io.mosip.kernel.keymanager.softhsm.util.CertificateUtility;

@RunWith(SpringRunner.class)
public class CertificateUtilityExceptionTest {

	BouncyCastleProvider provider;
	SecureRandom random;
	KeyPairGenerator keyGenerator;

	@Before
	public void setUp() throws Exception {

		provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		random = new SecureRandom();

	}

	@Test(expected = KeystoreProcessingException.class)
	public void testGenerateX509CertificateException() throws Exception {
		keyGenerator = KeyPairGenerator.getInstance("ELGAMAL", provider);
		keyGenerator.initialize(2048, random);
		CertificateUtility.generateX509Certificate(keyGenerator.generateKeyPair(), "commonName", "organizationalUnit",
				"organization", "country", LocalDateTime.now(), LocalDateTime.now().minusDays(100));
	}
}
