package org.mosip.registration.processor.packet.decryptor.job;

import static java.util.Arrays.copyOfRange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
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

import org.mosip.kernel.core.security.constants.MosipSecurityMethod;
import org.mosip.kernel.core.security.decryption.MosipDecryptor;
import org.mosip.kernel.core.security.exception.MosipInvalidDataException;
import org.mosip.kernel.core.security.exception.MosipInvalidKeyException;

public class MockDecryptor {
	private static byte[] sessionKey;
	private static byte[] encryptedData;
	private static final String PRIVATE_KEY="D:\\Data\\Data\\private.key";
	private static final String DATA="D:\\Data\\Data\\2018782130000125092018132756.zip";
	private static final String DDATA="D:\\Data\\Data\\2018782130000125092018132756_decrypted.zip";
	
	
	
	public static void main(String[] args) throws ClassNotFoundException   {
		InputStream prkstream = null;
		InputStream instream = null;
		OutputStream outstream=null;
		try {
			prkstream = new FileInputStream(new File(PRIVATE_KEY));
		
		byte[] prikey=new byte[prkstream.available()];
		prkstream.read(prikey);
		prkstream.close();
		
		instream= new FileInputStream(new File(DATA));
		byte[] in=new byte[instream.available()];
		instream.read(in);
		instream.close();
		
		System.out.println(in.length);
		splitKeyEncryptedData(in);
		
		byte[] aeskey=decryptRsaEncryptedBytes(sessionKey , readPrivatekey());
		System.out.println(aeskey.length);
		
		byte[] aesDecryptedData = MosipDecryptor.symmetricDecrypt(aeskey, encryptedData, MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		System.out.println(aesDecryptedData.length);
		
		outstream=new FileOutputStream(new File(DDATA));
		outstream.write(aesDecryptedData);
		
		outstream.close();
		
		
		
		} catch (IOException | MosipInvalidDataException | MosipInvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			
			e.printStackTrace();
		}
		
		}
	private static void splitKeyEncryptedData(final byte[] encryptedDataWithKey) {

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
	
	
	public static byte[] decryptRsaEncryptedBytes(final byte[] rsaEncryptedBytes,final PrivateKey privateKey) {
		 Cipher encryptCipher = null;
		 byte[] rsaDecryptedBytes=null;	
				
				try {
					encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
					
					encryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
						     
		        	rsaDecryptedBytes= encryptCipher.doFinal(rsaEncryptedBytes);
				} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
					
					e.printStackTrace();
				}
					        		        					
				return rsaDecryptedBytes;
	}
	
	private static PrivateKey readPrivatekey() throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			fileInputStream = new FileInputStream(new File(PRIVATE_KEY));
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		objectInputStream = new ObjectInputStream(fileInputStream);
		BigInteger mod = (BigInteger) objectInputStream.readObject();
		BigInteger exp = (BigInteger) objectInputStream.readObject();

		
		RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(mod, exp);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);

		objectInputStream.close();
		return privateKey;
	}
}
