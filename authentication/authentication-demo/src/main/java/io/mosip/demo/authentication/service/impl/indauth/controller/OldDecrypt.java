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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.demo.authentication.service.EncryptHelper.CryptoUtility;


@RestController
public class OldDecrypt {

	
	@PostMapping(path = "/authRequest/oldDecrypt")
	public String decrypt(@RequestBody String data)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		byte[] finalvalue=null;
		PrivateKey privateKey = fileReader();
		String encodedKey = data.substring(0, 343);
		String encodeData = data.substring(344, data.length()-1);
		
		return oldDecrypt(finalvalue, privateKey, encodedKey, encodeData);
	}

	
	/**
	 * Old decrypt.
	 *
	 * @param finalvalue the finalvalue
	 * @param privateKey the private key
	 * @param encodedKey the encoded key
	 * @param encodeData the encode data
	 * @return the string
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	private String oldDecrypt(byte[] finalvalue, PrivateKey privateKey, String encodedKey, String encodeData)
			throws NoSuchAlgorithmException {
		CryptoUtility cryptoUtil=new CryptoUtility();
		SecretKey secKey=null;;
		try {
			secKey = cryptoUtil.asymmetricDecrypt(privateKey, org.apache.commons.codec.binary.Base64.decodeBase64(encodedKey));
			 finalvalue = cryptoUtil.symmetricDecrypt(secKey, org.apache.commons.codec.binary.Base64.decodeBase64(encodeData));
		} catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			return new String(e.getMessage());
		}
		
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
