package org.mosip.registration.processor.packet.decryptor.job;

import static java.util.Arrays.copyOfRange;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.io.IOUtils;
import org.mosip.kernel.core.security.constants.MosipSecurityMethod;
import org.mosip.kernel.core.security.decryption.MosipDecryptor;
import org.mosip.kernel.core.security.exception.MosipInvalidDataException;
import org.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class Decryptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(Decryptor.class);

	private static final String LOGDISPLAY = "{} - {}";
	
	private  byte[] sessionKey;
	private  byte[] encryptedData;
@Value("${private.key.location}")
	private   String privateKey;
	
	

	/**random method for decryption
	 * @param encryptedPacket
	 * @param registrationId
	 * @return
	 */
	public InputStream decrypt(InputStream encryptedPacket, String registrationId) {
		
		InputStream outstream=null;
		try {
		
		byte[] in = IOUtils.toByteArray(encryptedPacket);
		
		
		
		splitKeyEncryptedData(in);
		
		byte[] aeskey=decryptRsaEncryptedBytes(sessionKey , readPrivatekey(registrationId));
		
		
		byte[] aesDecryptedData = MosipDecryptor.symmetricDecrypt(aeskey, encryptedData, MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		
		
		outstream= new ByteArrayInputStream(aesDecryptedData);
		
		
		
		} catch (IOException | MosipInvalidDataException | MosipInvalidKeyException e) {
			
			LOGGER.error(LOGDISPLAY, e);
		}
		return outstream;
	}



	private byte[] decryptRsaEncryptedBytes(final byte[] rsaEncryptedBytes,final PrivateKey privateKey) {
		
		Cipher encryptCipher = null;
		 byte[] rsaDecryptedBytes=null;	
				
				try {
					encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
					
					encryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
						     
		        	rsaDecryptedBytes= encryptCipher.doFinal(rsaEncryptedBytes);
				} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
					
					LOGGER.error(LOGDISPLAY, e);
				}
					        		        					
				return rsaDecryptedBytes;
	}



	private PrivateKey readPrivatekey(String registrationId) {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		PrivateKey rprivateKey=null;
		try {
			fileInputStream = new FileInputStream(new File(privateKey+"/"+registrationId+"/private.key"));
		} catch (FileNotFoundException e) {
			
			LOGGER.error(LOGDISPLAY, e);
		}
		try {
			objectInputStream = new ObjectInputStream(fileInputStream);
			BigInteger mod = (BigInteger) objectInputStream.readObject();
			BigInteger exp = (BigInteger) objectInputStream.readObject();

			
			RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(mod, exp);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			 rprivateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);

			objectInputStream.close();
		} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			
			LOGGER.error(LOGDISPLAY, e);
		}
		
		return rprivateKey;
	}



	private void splitKeyEncryptedData(final byte[] encryptedDataWithKey) {
		// Split the Key and Encrypted Data
				String keySplitter = "#KEY_SPLITTER#";
				int keyDemiliterIndex = 0;
				final int cipherKeyandDataLength = encryptedDataWithKey.length;
				final int keySplitterLength = keySplitter.length();

				final byte keySplitterFirstByte = keySplitter.getBytes()[0];
				for (; keyDemiliterIndex < cipherKeyandDataLength; keyDemiliterIndex++) {
					if (encryptedDataWithKey[keyDemiliterIndex] == keySplitterFirstByte) {
						final String keySplit = new String(
								copyOfRange(encryptedDataWithKey, keyDemiliterIndex, keyDemiliterIndex + keySplitterLength));
						if (keySplitter.equals(keySplit)) {
							break;
						}
					}
				}

				sessionKey = copyOfRange(encryptedDataWithKey, 0, keyDemiliterIndex);
				encryptedData = copyOfRange(encryptedDataWithKey, keyDemiliterIndex + keySplitterLength,
						cipherKeyandDataLength);
		
	}
	}