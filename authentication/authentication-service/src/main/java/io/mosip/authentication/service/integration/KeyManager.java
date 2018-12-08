package io.mosip.authentication.service.integration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;

/**
 * The Class KeyManager.
 * 
 * @author Sanjay Murali
 */
@Component
public class KeyManager {

	/** The Constant AES. */
	private static final String AES = "AES";
	
	/** The Constant SESSION_KEY. */
	private static final String SESSION_KEY = "sessionKey";
	
	/** The Constant KEY. */
	private static final String KEY = "key";
	
	/** The Constant RSA. */
	private static final String RSA = "RSA";
	
	/** The Constant REQUEST. */
	private static final String REQUEST = "request";
	
	/** The Constant TSP_ID. */
	private static final String TSP_ID = "tspID";
	
	/** The Constant FILEPATH. */
	private static final String FILEPATH = "sample.privatekey.filepath";

	/**
	 * Request data.
	 *
	 * @param requestBody the request body
	 * @param env the env
	 * @param decryptor the decryptor
	 * @param mapper the mapper
	 * @return the map
	 */
	public Map<String, Object> requestData(Map<String, Object> requestBody, Environment env, DecryptorImpl decryptor, ObjectMapper mapper) {
		Map<String, Object> request = null;
		try {
			String tspId = (String) requestBody.get(TSP_ID);
			byte[] privateKey = fileReader(tspId, env);
			byte[] reqBoby = (byte[]) requestBody.get(REQUEST);
			KeyFactory kf = KeyFactory.getInstance(RSA);
			PrivateKey priKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
			Optional<String> encryptedSessionKey = Optional.ofNullable(requestBody.get(KEY))
					.filter(obj -> obj instanceof Map)
					.map(obj -> String.valueOf(((Map<String, Object>)obj).get(SESSION_KEY)));
			if(encryptedSessionKey.isPresent()) {
				byte[] encyptedSessionkey = Base64.getDecoder().decode(encryptedSessionKey.get());
				byte[] decryptedKey = decryptor.asymmetricPrivateDecrypt(priKey, encyptedSessionkey);
				byte[] finalDecryptedData = decryptor
						.symmetricDecrypt(new SecretKeySpec(decryptedKey, 0, decryptedKey.length, AES), reqBoby);
				request = mapper.readValue(
						finalDecryptedData,
						new TypeReference<Map<String, Object>>() {
						});				
			}
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
		}
		return request;
	}

	/**
	 * File reader.
	 *
	 * @param filename the filename
	 * @param env the env
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte[] fileReader(String filename, Environment env) throws IOException {
		String localpath = env.getProperty(FILEPATH);
		Object[] homedirectory = new Object[] { System.getProperty("user.home") + File.separator };
		String finalpath = MessageFormat.format(localpath, homedirectory);
		File fileInfo = new File(finalpath + File.separator + filename);
		File parentFile = fileInfo.getParentFile();
		byte[] output = null;
		if (parentFile.exists()) {
			output = Files.readAllBytes(fileInfo.toPath());
		}
		return output;
	}
	
	public byte[] createHash(String data) {
		byte[] hashedData = HMACUtils.generateHash(data.getBytes());
		return hashedData;
	}

}
