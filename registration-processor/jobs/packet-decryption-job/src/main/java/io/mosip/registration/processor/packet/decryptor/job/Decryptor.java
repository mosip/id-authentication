package io.mosip.registration.processor.packet.decryptor.job;

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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.registration.processor.packet.decryptor.job.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.packet.decryptor.job.exception.constant.PacketDecryptionFailureExceptionConstant;
/**
 * Decryptor class for packet decryption
 * @author Jyoti Prakash Nayak
 *
 */
@Component
public class Decryptor {
	private  byte[] sessionKey;
	private  byte[] encryptedData;
	@Value("${private.key.location}")
	private   String privateKey;
	
	

	/**random method for decryption
	 * @param encryptedPacket
	 * @param registrationId
	 * @return decrypted packet data in InputStream
	 * @throws PacketDecryptionFailureException 
	 */
	public InputStream decrypt(InputStream encryptedPacket, String registrationId) throws PacketDecryptionFailureException {
		
		InputStream outstream=null;
		try {
		
		byte[] in = IOUtils.toByteArray(encryptedPacket);
		
		
		
		splitKeyEncryptedData(in);
		
		byte[] aeskey=decryptRsaEncryptedBytes(sessionKey , readPrivatekey(registrationId));
		
		
		byte[] aesDecryptedData = MosipDecryptor.symmetricDecrypt(aeskey, encryptedData, MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		
		
		outstream= new ByteArrayInputStream(aesDecryptedData);
		
		
		
		} catch (IOException | MosipInvalidDataException | MosipInvalidKeyException  e) {
			
			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorMessage(), e);
		}
		return outstream;
	}



	/**
	 * Method to decrypt the encrypted AEs session key
	 * @param rsaEncryptedBytes encypted AES session key
	 * @param privateKey private key to decrypt AES session key
	 * @return AES session key
	 * @throws PacketDecryptionFailureException 
	 */
	private byte[] decryptRsaEncryptedBytes(final byte[] rsaEncryptedBytes,final PrivateKey privateKey) throws PacketDecryptionFailureException {
		
		Cipher encryptCipher = null;
		 byte[] rsaDecryptedBytes=null;	
				
				try {
					encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
					
					encryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
						     
		        	rsaDecryptedBytes= encryptCipher.doFinal(rsaEncryptedBytes);
				} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
					
					throw new PacketDecryptionFailureException(
							PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
							PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorMessage(), e);
				}
					        		        					
				return rsaDecryptedBytes;
	}



	/**
	 * Method to read private key from private key file
	 * @param registrationId registarion id of the packet
	 * @return private key 
	 * @throws PacketDecryptionFailureException 
	 */
	private PrivateKey readPrivatekey(String registrationId) throws PacketDecryptionFailureException {
		FileInputStream fileInputStream = null;
		PrivateKey rprivateKey=null;
		try {
			fileInputStream = new FileInputStream(new File(privateKey+registrationId+"/private.key"));
		} catch (FileNotFoundException e) {
			
			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorMessage(), e);
		}
		try(ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);) {
			
			BigInteger mod = (BigInteger) objectInputStream.readObject();
			BigInteger exp = (BigInteger) objectInputStream.readObject();

			
			RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(mod, exp);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			 rprivateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);

			
		} catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			
			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorMessage(), e);
		}
		
		return rprivateKey;
	}



	/**
	 * Method to separate encrypted data and encrypted AES session key in encrypted packet
	 * @param encryptedDataWithKey encrypted packet containing encrypted data and encrypted AES session key
	 */
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