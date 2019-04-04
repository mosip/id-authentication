package io.mosip.kernel.responsesignature.api.util;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;


/**
 *  SigningUtil class.
 *  @author Srinivasan
 *  @since 1.0.0
 */
//@Component
public class SigningUtil {

	/** instance of rest template. *//*
	@Autowired
	private RestTemplate restTemplate;

	*//** instance of keygenerator. *//*
	@Autowired
	KeyGenerator keygenerator;

	*//** instance of encryptor. *//*
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;
	
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	*//**  crypto encypt url. *//*
	@Value("{io.mosip.kernel.crytpomanager-service-url:https://dev.mosip.io/cryptomanager/encrypt}")
	private String cryptoEncyptUrl;

	*//**
	 * Generate key pair.
	 *
	 * @param response the response
	 * @return the key pair
	 *//*
	public KeyPair generateKeyPair(String response) {
		return keygenerator.getAsymmetricKey();
	}
	*/
	
	

}
