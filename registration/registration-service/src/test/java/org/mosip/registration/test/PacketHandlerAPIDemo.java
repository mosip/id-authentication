package org.mosip.registration.test;

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
import org.mosip.kernel.core.security.constants.MosipSecurityMethod;
import org.mosip.kernel.core.security.decryption.MosipDecryptor;
import org.mosip.kernel.core.security.exception.MosipInvalidDataException;
import org.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import org.mosip.kernel.core.utils.DateUtil;
import org.mosip.kernel.core.utils.FileUtil;
import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.dto.RegistrationDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.service.PacketCreationService;
import org.mosip.registration.service.PacketEncryptionService;
import org.mosip.registration.test.config.SpringConfiguration;
import org.mosip.registration.test.util.datastub.DataProvider;
import org.mosip.registration.test.util.rsa.RSADecryption;
import org.mosip.registration.util.reader.PropertyFileReader;
import org.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;

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
		String dateInString = DateUtil.formatDate(new Date(),
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

		FileUtil.copyToFile(new ByteArrayInputStream(aesDecryptedData),
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
