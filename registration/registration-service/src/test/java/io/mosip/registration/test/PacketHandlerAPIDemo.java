package io.mosip.registration.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import static java.util.Arrays.copyOfRange;

import org.junit.Test;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.registration.test.config.SpringConfiguration;
import io.mosip.registration.test.util.datastub.DataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;

public class PacketHandlerAPIDemo extends SpringConfiguration {

	@Autowired
	private Environment environment;
	@Autowired
	private PacketHandlerService packetHandlerService;
	@Autowired
	private RSAKeyGenerator rsaKeyGenerator;
	private byte[] sessionKey;
	private byte[] encryptedData;

	@Test
	public void testHandle() throws MosipInvalidDataException, MosipInvalidKeyException, IOException,
			URISyntaxException, RegBaseCheckedException, MosipIOException {
		SessionContext sessionContext = SessionContext.getInstance();
		sessionContext.getUserContext().setUserId("mosip");
		sessionContext.getUserContext().setName("mosip");
		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", sessionContext);
		RegistrationDTO registrationDTO = DataProvider.getPacketDTO();
		registrationDTO.setRegistrationId(registrationDTO.getRegistrationId());
		packetHandlerService.handle(registrationDTO);
		String registrationId = registrationDTO.getRegistrationId();

		// Decryption
		String dateInString = DateUtils.formatDate(new Date(),
				environment.getProperty(RegistrationConstants.PACKET_STORE_DATE_FORMAT));
		String inputZipPath = environment.getProperty(RegistrationConstants.PACKET_STORE_LOCATION) + File.separator
				+ dateInString + File.separator + registrationId + RegistrationConstants.ZIP_FILE_EXTENSION;
		String outputZip = "Uncompressed" + File.separator + dateInString;

		FileInputStream fileInputStream = new FileInputStream(new File(inputZipPath));
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

		byte[] rsaEncryptedData = new byte[bufferedInputStream.available()];
		bufferedInputStream.read(rsaEncryptedData);
		bufferedInputStream.close();

		splitKeyEncryptedData(rsaEncryptedData);

		byte[] rsaDecryptedData = MosipDecryptor.asymmetricPrivateDecrypt(rsaKeyGenerator.getEncodedKey(false),
				sessionKey, MosipSecurityMethod.RSA_WITH_PKCS1PADDING);
		byte[] aesDecryptedData = MosipDecryptor.symmetricDecrypt(rsaDecryptedData, encryptedData,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);

		FileUtils.copyToFile(new ByteArrayInputStream(aesDecryptedData),
				new File(outputZip + File.separator + registrationId + RegistrationConstants.ZIP_FILE_EXTENSION));
	}

	private void splitKeyEncryptedData(final byte[] encryptedDataWithKey) {

		// Split the Key and Encrypted Data
		String keySplitter = environment.getProperty(RegistrationConstants.AES_KEY_CIPHER_SPLITTER);
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

		this.sessionKey = copyOfRange(encryptedDataWithKey, 0, keyDemiliterIndex);
		this.encryptedData = copyOfRange(encryptedDataWithKey, keyDemiliterIndex + keySplitterLength,
				cipherKeyandDataLength);
	}

}
