package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.MessageFormat;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.io.Files;

import io.mosip.demo.authentication.service.dto.EncryptionRequestDto;
import io.mosip.demo.authentication.service.dto.EncryptionResponseDto;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Dinesh Karuppiah
 */

@RestController
public class Encrypt {

	private static final String FILEPATH = "sample.privatekey.filepath";
	private static final String FORMAT = "UTF-8";

	@Autowired
	private Environment environment;

	@Autowired
	private KeyGenerator keyGenerator;

	@Autowired
	private EncryptorImpl encryptor;

	private static final Provider provider = new BouncyCastleProvider();

	@GetMapping(path = "/sessionKey")
	@ApiOperation(value = "Generate Session Key")
	public String getSessionKey() {
		SecretKey symmetricKey = keyGenerator.getSymmetricKey();
		return Base64.getEncoder().encodeToString(symmetricKey.getEncoded());
	}

	@GetMapping(path = "/publicKey")
	@ApiOperation(value = "Generate Public Key", response = String.class)
	public String getPublicKey(String tspId, String date) {
		KeyPair asymmetricKey = keyGenerator.getAsymmetricKey();
		PublicKey publickey = asymmetricKey.getPublic();
		byte[] privateKey = asymmetricKey.getPrivate().getEncoded();
		storePrivateKey(privateKey, tspId);
		return Base64.getEncoder().encodeToString(publickey.getEncoded());
	}

	@PostMapping(path = "/authRequest/encrypt")
	@ApiOperation(value = "Encrypt Identity with sessionKey and Encrypt Session Key with Public Key", response = EncryptionResponseDto.class)
	public EncryptionResponseDto encrypt(@RequestBody EncryptionRequestDto encryptionRequestDto) {
		EncryptionResponseDto encryptionResponseDto = new EncryptionResponseDto();
		byte[] sessionKey = decode(encryptionRequestDto.getSessionKey());
		byte[] tmpPublicKey = decode(encryptionRequestDto.getPublicKey());
		PublicKey publicKey = null;
		SecretKeySpec secretKey = null;
		try {
			publicKey = preparePublicKey(tmpPublicKey);
			secretKey = prepareSecretKey(sessionKey, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (InvalidKeySpecException e2) {
			e2.printStackTrace();
		}

		// Encrypt data with session key
		byte[] data = encryptionRequestDto.getIdentityRequest().getBytes();
		byte[] encryptedData = encryptor.symmetricEncrypt(secretKey, data);
		encryptionResponseDto.setEncryptedData(encryptedData);

		// Encrypt session Key with public Key
		byte[] encryptedsessionKey = encryptor.asymmetricPublicEncrypt(publicKey, sessionKey);
		encryptionResponseDto.setEncryptedkey(encryptedsessionKey);

		return encryptionResponseDto;
	}

	private PublicKey preparePublicKey(byte[] publicKeyStr) throws InvalidKeySpecException, NoSuchAlgorithmException {
		return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyStr));
	}

	private SecretKeySpec prepareSecretKey(byte[] sessionKey, String type)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digester;
		digester = MessageDigest.getInstance("SHA-256", provider);
		digester.update(String.valueOf(sessionKey).getBytes(FORMAT));
		byte[] key = digester.digest();
		SecretKeySpec spec = new SecretKeySpec(key, type);
		return spec;
	}

	private byte[] decode(String value) {
		return Base64.getDecoder().decode(value);
	}

	private void storePrivateKey(byte[] encodedvalue, String tspId) {
		String localpath = environment.getProperty(FILEPATH);
		Object[] homedirectory = new Object[] { System.getProperty("user.home") + File.separator };
		String finalpath = MessageFormat.format(localpath, homedirectory);
		BufferedWriter output = null;
		try {
			File fileInfo = new File(finalpath + File.separator + tspId);
			File parentFile = fileInfo.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			Files.write(encodedvalue, fileInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
