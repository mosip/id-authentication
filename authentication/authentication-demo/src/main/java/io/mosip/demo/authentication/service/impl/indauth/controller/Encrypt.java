package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.crypto.bouncycastle.constant.SecurityMethod;
import io.mosip.kernel.crypto.bouncycastle.impl.EncryptorImpl;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

@RestController
public class Encrypt {

	private static final String FILE_NAME = "/TSP.txt";

	private static final String FORMAT = "UTF-8";

	@Autowired
	Environment environment;

	private EncryptorImpl encryptorImpl;

	@GetMapping(path = "/sessionKey")
	public String generateSessionKey() {
		SecretKey symmetricKey = KeyGenerator.getSymmetricKey();
		return Base64.getEncoder().encodeToString(symmetricKey.getEncoded());
	}

	@GetMapping(path = "/publicKey")
	public String getpublicKey(String tspId, String date) {
		KeyPair asymmetricKey = KeyGenerator.getAsymmetricKey();
		PublicKey publickey = asymmetricKey.getPublic();
		byte[] privateKey = asymmetricKey.getPrivate().getEncoded();
		storePrivateKey(privateKey);
		return Base64.getEncoder().encodeToString(publickey.getEncoded());
	}

	@PostMapping(path = "/authRequest/encrypt")
	public String encrypt(@RequestBody String identityRequest, @RequestBody String sessionKey, String publicKey) {
		byte[] decodedSessionKey = decode(sessionKey);
		byte[] decodedPublicKey = decode(publicKey);
		byte[] asymmetricPrivateEncrypt = encryptorImpl.asymmetricPrivateEncrypt(decodedSessionKey, decodedPublicKey,
				SecurityMethod.RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING);

		 // Decode session Key, Encrypt IdentityRequest
		// Decode Public Key, Encrypt session Key
		// Send Encrypt IdentityRequest and Encrypt session Key
//		encryptorImpl.asymmetricPublicEncrypt(publicKey, data, mosipSecurityMethod)
		return null;
	}

	private byte[] decode(String value) {
		return Base64.getDecoder().decode(value);
	}

	private void storePrivateKey(byte[] encodedvalue) {
		String filepath = environment.getProperty("");
		BufferedWriter output = null;
		try {
			File fileInfo = new File(filepath + FILE_NAME);
			output = new BufferedWriter(new FileWriter(fileInfo));
			String privateKey = new String(encodedvalue, FORMAT);
			output.write(privateKey);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
