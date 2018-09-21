package org.mosip.registration.processor.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Arrays.copyOfRange;
import static org.mosip.registration.processor.consts.RegConstants.AES_KEY_CIPHER_SPLITTER;
import static org.mosip.registration.processor.consts.RegConstants.PACKET_STORE_DATE_FORMAT;
import static org.mosip.registration.processor.consts.RegConstants.PACKET_STORE_LOCATION;
import static org.mosip.registration.processor.consts.RegConstants.PACKET_UNZIP_LOCATION;
import static org.mosip.registration.processor.consts.RegConstants.ZIP_FILE_EXTENSION;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;
import org.mosip.registration.processor.config.SpringConfiguration;
import org.mosip.registration.processor.exception.RegBaseCheckedException;
import org.mosip.registration.processor.service.packet.creation.PacketCreationManager;
import org.mosip.registration.processor.service.packet.encryption.PacketEncryptionManager;
import org.mosip.registration.processor.test.util.aes.AESDecryption;
import org.mosip.registration.processor.test.util.datastub.DataProvider;
import org.mosip.registration.processor.test.util.rsa.RSADecryption;
import org.mosip.registration.processor.dto.EnrollmentDTO;
import org.mosip.registration.processor.consts.RegConstants;
import org.mosip.registration.processor.util.reader.PropertyFileReader;
import org.mosip.registration.processor.util.rsa.keygenerator.RSAKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;

public class PacketHandlerAPITest extends SpringConfiguration {

	@Autowired
	private PacketCreationManager packetCreationManager;
	@Autowired
	private PacketEncryptionManager packetEncryptionManager;
	@Autowired
	private RSADecryption rsaDecryption;
	@Autowired
	private AESDecryption aesDecryption;
	@Autowired
	private RSAKeyGenerator rsaKeyGenerator;
	private byte[] sessionKey;
	private byte[] encryptedData;

	@Test
	public void testHandle() throws URISyntaxException, IOException, RegBaseCheckedException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException {
		EnrollmentDTO enrollmentDTO = DataProvider.getEnrollmentDTO();
		byte[] zippedPacket = packetCreationManager.create(enrollmentDTO);
		String enrollmentId = enrollmentDTO.getPacketDTO().getEnrollmentID();
		packetEncryptionManager.encrypt(enrollmentDTO, zippedPacket);

		// Decryption
		String dateInString = new SimpleDateFormat(PropertyFileReader.getPropertyValue(PACKET_STORE_DATE_FORMAT))
				.format(new Date());
		String inputZipPath = PropertyFileReader.getPropertyValue(PACKET_STORE_LOCATION) + File.separator + dateInString
				+ File.separator + enrollmentId + ZIP_FILE_EXTENSION;
		String outputZip = PropertyFileReader.getPropertyValue(PACKET_UNZIP_LOCATION) + File.separator + dateInString;
		
		FileInputStream fileInputStream = new FileInputStream(new File(inputZipPath));
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

		byte[] rsaEncryptedData = new byte[bufferedInputStream.available()];
		bufferedInputStream.read(rsaEncryptedData);
		bufferedInputStream.close();

		splitKeyEncryptedData(rsaEncryptedData);

		byte[] rsaDecryptedData = rsaDecryption.decryptRsaEncryptedBytes(sessionKey,
				rsaKeyGenerator.readPrivatekey(RegConstants.RSA_PRIVATE_KEY_FILE));
		byte[] aesDecryptedData = aesDecryption.decrypt(encryptedData, rsaDecryptedData);
		
		File outputDir = new File(outputZip);
		if(!outputDir.exists()) {
			outputDir.mkdir();
		}

		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(
				outputZip + File.separator + enrollmentId + ZIP_FILE_EXTENSION));
		bufferedOutputStream.write(aesDecryptedData);
		bufferedOutputStream.flush();
		bufferedOutputStream.close();
	}

	private void splitKeyEncryptedData(final byte[] encryptedDataWithKey) {

		// Split the Key and Encrypted Data
		String keySplitter = PropertyFileReader.getPropertyValue(AES_KEY_CIPHER_SPLITTER);
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
