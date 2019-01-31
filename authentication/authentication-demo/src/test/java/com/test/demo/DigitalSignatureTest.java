package com.test.demo;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.jose4j.lang.JoseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.demo.authentication.service.impl.indauth.controller.DigitalSign;


/**
 * @author Arun Bose S
 * The Class DigitalSignatureTest.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class DigitalSignatureTest {
	
	/** The digital sign mock. */
	@InjectMocks
	private DigitalSign digitalSignMock;
	
	
	/**
	 * Digital sign test.
	 *
	 * @throws KeyStoreException the key store exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws CertificateException the certificate exception
	 * @throws UnrecoverableEntryException the unrecoverable entry exception
	 * @throws InvalidKeySpecException the invalid key spec exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JoseException the jose exception
	 */
	@Test
	public void digitalSignTest() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, InvalidKeySpecException, IOException, JoseException {
		digitalSignMock.sign("sdfsdfsdfsdf");
	}

	
}
