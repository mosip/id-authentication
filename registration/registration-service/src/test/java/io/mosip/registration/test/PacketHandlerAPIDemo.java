package io.mosip.registration.test;

import static java.util.Arrays.copyOfRange;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import io.mosip.kernel.core.exception.MosipIOException;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.test.config.SpringConfiguration;
import io.mosip.registration.test.util.datastub.DataProvider;
import io.mosip.registration.test.util.rsa.RSADecryption;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.PacketCreationService;
import io.mosip.registration.service.PacketEncryptionService;
import io.mosip.registration.util.reader.PropertyFileReader;
import io.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;

public class PacketHandlerAPIDemo extends SpringConfiguration {

	@Autowired
	private PacketCreationService packetCreationManager;
	@Autowired
	private PacketEncryptionService packetEncryptionManager;
	@Autowired
	private RSADecryption rsaDecryption;
	@Autowired
	private RSAKeyGenerator rsaKeyGenerator;
	private byte[] sessionKey;
	private byte[] encryptedData;

	@Test
	public void testHandle() throws MosipInvalidDataException, MosipInvalidKeyException, IOException,
			URISyntaxException, RegBaseCheckedException, MosipIOException {
		RegistrationDTO enrollmentDTO = DataProvider.getPacketDTO();
		byte[] zippedPacket = packetCreationManager.create(enrollmentDTO);
		String registrationId = enrollmentDTO.getRegistrationId().replaceAll("[^0-9]", "");
		packetEncryptionManager.encrypt(enrollmentDTO, zippedPacket);

		// Decryption
		String dateInString = DateUtils.formatDate(new Date(),
				PropertyFileReader.getPropertyValue(RegConstants.PACKET_STORE_DATE_FORMAT));
		String inputZipPath = PropertyFileReader.getPropertyValue(RegConstants.PACKET_STORE_LOCATION) + File.separator + dateInString
				+ File.separator + registrationId + RegConstants.ZIP_FILE_EXTENSION;
		String outputZip = PropertyFileReader.getPropertyValue(RegConstants.PACKET_UNZIP_LOCATION) + File.separator + dateInString;

		FileInputStream fileInputStream = new FileInputStream(new File(inputZipPath));
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

		byte[] rsaEncryptedData = new byte[bufferedInputStream.available()];
		bufferedInputStream.read(rsaEncryptedData);
		bufferedInputStream.close();

		splitKeyEncryptedData(rsaEncryptedData);

		byte[] rsaDecryptedData = rsaDecryption.decryptRsaEncryptedBytes(sessionKey,
				rsaKeyGenerator.readPrivatekey(RegConstants.RSA_PRIVATE_KEY_FILE));
		byte[] aesDecryptedData = MosipDecryptor.symmetricDecrypt(rsaDecryptedData, encryptedData,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);

		FileUtils.copyToFile(new ByteArrayInputStream(aesDecryptedData),
				new File(outputZip + File.separator + registrationId + RegConstants.ZIP_FILE_EXTENSION));
	}

	private void splitKeyEncryptedData(final byte[] encryptedDataWithKey) {

		// Split the Key and Encrypted Data
		String keySplitter = PropertyFileReader.getPropertyValue(RegConstants.AES_KEY_CIPHER_SPLITTER);
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
