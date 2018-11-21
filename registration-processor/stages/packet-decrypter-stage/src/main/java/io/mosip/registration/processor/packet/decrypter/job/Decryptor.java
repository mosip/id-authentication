package io.mosip.registration.processor.packet.decrypter.job;

import static java.util.Arrays.copyOfRange;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
 * 
 * @author Jyoti Prakash Nayak
 *
 */
@Component
public class Decryptor {
	private byte[] sessionKey;
	private byte[] encryptedData;
	@Value("${registration.processor.private.key.location}")
	private String privateKey;

	/**
	 * random method for decryption
	 * 
	 * @param encryptedPacket
	 * @param registrationId
	 * @return decrypted packet data in InputStream
	 * @throws PacketDecryptionFailureException
	 */
	public InputStream decrypt(InputStream encryptedPacket, String registrationId)
			throws PacketDecryptionFailureException {

		InputStream outstream = null;
		try {

			byte[] in = IOUtils.toByteArray(encryptedPacket);

			splitKeyEncryptedData(in);

			byte[] aeskey = MosipDecryptor.asymmetricPrivateDecrypt(readPrivatekey(registrationId), sessionKey,
					MosipSecurityMethod.RSA_WITH_PKCS1PADDING);

			byte[] aesDecryptedData = MosipDecryptor.symmetricDecrypt(aeskey, encryptedData,
					MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);

			outstream = new ByteArrayInputStream(aesDecryptedData);

		} catch (Exception e) {

			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
							.getErrorMessage(),
					e);
		}
		return outstream;
	}

	/**
	 * Method to read private key from private key file
	 * 
	 * @param registrationId
	 *            registarion id of the packet
	 * @return private key
	 * @throws PacketDecryptionFailureException
	 */
	private byte[] readPrivatekey(String registrationId) throws PacketDecryptionFailureException {
		FileInputStream fileInputStream = null;
		byte[] rprivateKey = null;
		try {
			fileInputStream = new FileInputStream(new File(privateKey + registrationId + "/private.key"));
			rprivateKey = IOUtils.toByteArray(fileInputStream);
		} catch (IOException e) {

			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
							.getErrorMessage(),
					e);
		}

		return rprivateKey;
	}

	/**
	 * Method to separate encrypted data and encrypted AES session key in encrypted
	 * packet
	 * 
	 * @param encryptedDataWithKey
	 *            encrypted packet containing encrypted data and encrypted AES
	 *            session key
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