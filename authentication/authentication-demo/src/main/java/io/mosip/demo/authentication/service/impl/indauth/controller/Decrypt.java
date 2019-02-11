package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.demo.authentication.service.EncryptHelper.CryptoUtility;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;;


/**
 * 
 * @author Arun Bose S
 * @author Sanjay Murali
 * The Class Decrypt.
 */
@RestController
public class Decrypt {

	/** The environment. */
	@Autowired
	Environment environment;

	/** The decryptor impl. */
	@Autowired
	DecryptorImpl decryptorImpl;

	/**
	 * Decrypt.
	 *
	 * @param data the data
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InvalidKeySpecException the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	@PostMapping(path = "/authRequest/decrypt")
	public String decrypt(@RequestBody String data)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		byte[] finalvalue=null;
		PrivateKey privateKey = fileReader();
		String encodedKey = data.substring(0, 343);
		String encodeData = data.substring(344, data.length()-1);
		
		return kernelDecrypt(finalvalue, privateKey, encodedKey, encodeData);
	}

	
	/**
	 * Kernel decrypt.
	 *
	 * @param finalvalue the finalvalue
	 * @param privateKey the private key
	 * @param encodedKey the encoded key
	 * @param encodeData the encode data
	 * @return the string
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	private String kernelDecrypt(byte[] finalvalue, PrivateKey privateKey, String encodedKey, String encodeData)
			throws NoSuchAlgorithmException {
		byte[] key = decryptorImpl.asymmetricPrivateDecrypt(privateKey, org.apache.commons.codec.binary.Base64.decodeBase64(encodedKey));
			 finalvalue = decryptorImpl.symmetricDecrypt(new SecretKeySpec(key, 0, key.length, "AES"), org.apache.commons.codec.binary.Base64.decodeBase64(encodeData));
				return new String(finalvalue);
	}
	




	
	
	
	
	

	/**
	 * File reader.
	 *
	 * @return the private key
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InvalidKeySpecException the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public PrivateKey fileReader() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		FileInputStream pkeyfis = new FileInputStream("lib/Keystore/privkey1.pem");
		String pKey = getFileContent(pkeyfis, "UTF-8");
		pKey = pKey.replaceAll("-----BEGIN (.*)-----\n", "");
		pKey = pKey.replaceAll("-----END (.*)----\n", "");
		pKey = pKey.replaceAll("\\s", "");
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pKey)));
	}
	
	/**
	 * Gets the file content.
	 *
	 * @param fis the fis
	 * @param encoding the encoding
	 * @return the file content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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
