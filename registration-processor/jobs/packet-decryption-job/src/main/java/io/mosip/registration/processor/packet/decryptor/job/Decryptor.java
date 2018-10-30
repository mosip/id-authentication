package io.mosip.registration.processor.packet.decryptor.job;

import static java.util.Arrays.copyOfRange;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.registration.processor.core.builder.CoreAuditRequestBuilder;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
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
	@Value("${private.key.location}")
	private String privateKey;
	
	/** The event id. */
	private String eventId = "";
	
	/** The event name. */
	private String eventName = "";
	
	/** The event type. */
	private String eventType = "";
	
	/** The description. */
	private String description = "";
	
	/** The core audit request builder. */
	@Autowired
	CoreAuditRequestBuilder coreAuditRequestBuilder;

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
			eventId = EventId.RPR_402.toString();
			eventName = EventName.UPDATE.toString();
			eventType = EventType.BUSINESS.toString();
			description = "Split the key and encrypted data success";
			byte[] aeskey = MosipDecryptor.asymmetricPrivateDecrypt(readPrivatekey(registrationId), sessionKey,
					MosipSecurityMethod.RSA_WITH_PKCS1PADDING);

			byte[] aesDecryptedData = MosipDecryptor.symmetricDecrypt(aeskey, encryptedData,
					MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);

			outstream = new ByteArrayInputStream(aesDecryptedData);

		} catch (IOException | MosipInvalidDataException | MosipInvalidKeyException e) {
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.SYSTEM.toString();
			description = "Invalid data and key exception while decrypting the packet failure";
			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
							.getErrorMessage(),
					e);
		} finally {
			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);
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
			eventId = EventId.RPR_401.toString();
			eventName = EventName.GET.toString();
			eventType = EventType.BUSINESS.toString();
			description = "Read private key from private key file success";
		} catch (IOException e) {
			eventId = EventId.RPR_405.toString();
			eventName = EventName.EXCEPTION.toString();
			eventType = EventType.SYSTEM.toString();
			description = "Read private key from private key file failure";
			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
							.getErrorMessage(),
					e);
		}finally {
			coreAuditRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);
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