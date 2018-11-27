package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.crypto.bouncycastle.impl.EncryptorImpl;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

@RestController
public class Encrypt {

	@Autowired
	Environment environment;

	private EncryptorImpl encryptorImpl;

	@GetMapping(path = "/sessionKey")
	public String getsessionKey() {
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
		byte[] decodedSessionKey = Base64.getDecoder().decode(sessionKey);
		
		// Decode session Key, Encrypt IdentityRequest
		// Decode Public Key, Encrypt session Key
		// Send Encrypt IdentityRequest and Encrypt session Key
//		encryptorImpl.asymmetricPublicEncrypt(publicKey, data, mosipSecurityMethod)
		return null;
	}

	private void storePrivateKey(byte[] encodedvalue) {
		String filepath = environment.getProperty("");
		BufferedWriter output = null;
		try {
			File fileInfo = new File(filepath + "/TSP.txt");
			output = new BufferedWriter(new FileWriter(fileInfo));
			String privateKey = new String(encodedvalue, "UTF-8");
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
