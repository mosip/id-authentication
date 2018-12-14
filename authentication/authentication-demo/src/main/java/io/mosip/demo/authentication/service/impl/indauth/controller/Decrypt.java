package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.demo.authentication.service.dto.EncryptionResponseDto;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;
import io.swagger.annotations.ApiOperation;;

@RestController
public class Decrypt {

	@Autowired
	Environment environment;

	@Autowired
	DecryptorImpl decryptorImpl;

	private static final String FILEPATH = "sample.privatekey.filepath";

	@PostMapping(path = "/authRequest/decrypt")
	@ApiOperation(value = "Decrypt Session Key with private Key and Decrypt Identity with sessionKey", response = EncryptionResponseDto.class)
	public String decrypt(String key, String data, String tspId)
			throws IOException {
		byte[] privateKey = fileReader(tspId);
		// Decrypt session Key with private Key
		String sessionKey = decrypt(privateKey, key.getBytes(), "sessionkey");

		// Decrypt data with with decrypted session key
		String finalvalue = decrypt(sessionKey.getBytes(), data.getBytes(), "data");

		System.err.println(finalvalue);
		return finalvalue;
	}

	private String decrypt(byte[] key, byte[] data, String type) {
		return decryptorImpl.symmetricDecrypt(prepareKey(key), data).toString();
	}

	private SecretKey prepareKey(byte[] key) {
		return new SecretKeySpec(key, 0, key.length, "AES");
	}

	public byte[] fileReader(String filename) throws IOException {
		String localpath = environment.getProperty(FILEPATH);
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

}
