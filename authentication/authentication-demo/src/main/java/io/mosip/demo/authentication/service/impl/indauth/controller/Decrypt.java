package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;;

@RestController
public class Decrypt {

	@Autowired
	Environment environment;

	@Autowired
	DecryptorImpl decryptorImpl;

	@PostMapping(path = "/authRequest/decrypt")
	public String decrypt(@RequestBody String data)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		PrivateKey privateKey = fileReader();
		String encodedKey = data.substring(0, 343);
		String encodeData = data.substring(344, data.length()-1);
		byte[] key = decryptorImpl.asymmetricPrivateDecrypt(privateKey, org.apache.commons.codec.binary.Base64.decodeBase64(encodedKey));
		byte[] finalvalue = decryptorImpl.symmetricDecrypt(new SecretKeySpec(key, 0, key.length, "AES"), org.apache.commons.codec.binary.Base64.decodeBase64(encodeData));
		return new String(finalvalue);
	}

	public PrivateKey fileReader() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		FileInputStream pkeyfis = new FileInputStream("lib/Keystore/privkey1.pem");
		String pKey = getFileContent(pkeyfis, "UTF-8");
		pKey = pKey.replaceAll("-----BEGIN (.*)-----\n", "");
		pKey = pKey.replaceAll("-----END (.*)----\n", "");
		pKey = pKey.replaceAll("\\s", "");
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pKey)));
	}
	
	public static String getFileContent(FileInputStream fis,String encoding ) throws IOException
	{
		try( BufferedReader br =
				new BufferedReader( new InputStreamReader(fis, encoding )))
		{
			StringBuilder sb = new StringBuilder();
			String line;
			while(( line = br.readLine()) != null ) {
				sb.append( line );
				sb.append( '\n' );
			}
			return sb.toString();
		}
	}

}
