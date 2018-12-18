package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Map;

import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

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

	/** The Constant RSA. */
	private static final String RSA = "RSA";

	@Autowired
	private Environment environment;

	@Autowired
	private KeyGenerator keyGenerator;

	@Autowired
	private EncryptorImpl encryptor;

	private static final Provider provider = new BouncyCastleProvider();

	@PostMapping(path = "/identity/encrypt")
	@ApiOperation(value = "Encrypt Identity with sessionKey and Encrypt Session Key with Public Key", response = EncryptionResponseDto.class)
	public EncryptionResponseDto encrypt(@RequestBody EncryptionRequestDto encryptionRequestDto)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		EncryptionResponseDto encryptionResponseDto = new EncryptionResponseDto();

		SecretKey sessionKey = keyGenerator.getSymmetricKey();
		ObjectMapper objMapper = new ObjectMapper();
		// Encrypt data with session key
		Map<String, Object> identityRequest = encryptionRequestDto.getIdentityRequest();
		byte[] data = objMapper.writeValueAsBytes(identityRequest);
		byte[] encryptedData = encryptor.symmetricEncrypt(sessionKey, data);
		encryptionResponseDto.setEncryptedIdentity(Base64.getEncoder().encodeToString(encryptedData));

//		KeyPair asymmetricKey = keyGenerator.getAsymmetricKey();
//		PublicKey publicKey = asymmetricKey.getPublic();
//		byte[] privateKey = asymmetricKey.getPrivate().getEncoded();
//		storePrivateKey(privateKey, encryptionRequestDto.getTspID());
		PublicKey publicKey = loadPublicKey();

		// Encrypt session Key with public Key
		byte[] encryptedsessionKey = encryptor.asymmetricPublicEncrypt(publicKey, sessionKey.getEncoded());
		encryptionResponseDto.setEncryptedSessionKey(Base64.getEncoder().encodeToString(encryptedsessionKey));

		return encryptionResponseDto;
	}

	private PublicKey loadPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		byte[] publicKeyBytes = getPublicKey("public_key", environment);
		PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
		return publicKey;
	}

//	private void storePrivateKey(byte[] encodedvalue, String tspId) {
//		String localpath = environment.getProperty(FILEPATH);
//		Object[] homedirectory = new Object[] { System.getProperty("user.home") + File.separator };
//		String finalpath = MessageFormat.format(localpath, homedirectory);
//		BufferedWriter output = null;
//		try {
//			File fileInfo = new File(finalpath + File.separator + tspId);
//			File parentFile = fileInfo.getParentFile();
//			if (!parentFile.exists()) {
//				parentFile.mkdirs();
//			}
//			Files.write(fileInfo.toPath(), encodedvalue);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public byte[] getPublicKey(String filename, Environment env) throws IOException {
		String localpath = env.getProperty(FILEPATH);
		Object[] homedirectory = new Object[] { System.getProperty("user.home") + File.separator };
		String finalpath = MessageFormat.format(localpath, homedirectory);
		File fileInfo = new File(finalpath + File.separator + filename);
		byte[] output = null;
		if (fileInfo.exists()) {
			output = Files.readAllBytes(fileInfo.toPath());
		} else {
			throw new IOException();
		}
		return output;
	}

}
