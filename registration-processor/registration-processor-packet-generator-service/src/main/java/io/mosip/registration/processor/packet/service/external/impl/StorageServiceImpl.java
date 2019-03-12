package io.mosip.registration.processor.packet.service.external.impl;

import static io.mosip.kernel.core.util.DateUtils.formatDate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.processor.packet.service.constants.RegistrationConstants;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.packet.service.external.StorageService;

/**
 * Class to store the encrypted packet and acknowledgement receipt of the
 * Registration in local disk
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class StorageServiceImpl implements StorageService {

	// private static final Logger LOGGER =
	// AppConfig.getLogger(StorageServiceImpl.class);

	@Value("${registration.processor.packet.storageLocation.encrypted}")
	private String packetStoreLocationEncrypted;

	@Value("${registration.processor.packet.storageLocation.decrypted}")
	private String packetStoreLocationdecrypted;

	@Autowired
	private Environment environment;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.StorageService#storeToDisk(java.lang.String,
	 * byte[], byte[])
	 */
	@Override
	public String storeToDisk(String registrationId, byte[] packet, boolean encrypted) throws RegBaseCheckedException {
		try {
			// Generate the file path for storing the Encrypted Packet and Acknowledgement
			// Receipt
			String seperator = "/";
			String filePath = "";
			if (encrypted) {
				filePath = packetStoreLocationEncrypted + seperator
						+ formatDate(new Date(),
								environment.getProperty(RegistrationConstants.PACKET_STORE_DATE_FORMAT))
										.concat(seperator).concat(registrationId);
			} else {
				filePath = packetStoreLocationdecrypted + seperator
						+ formatDate(new Date(),
								environment.getProperty(RegistrationConstants.PACKET_STORE_DATE_FORMAT))
										.concat(seperator).concat(registrationId);
			}
			// Storing the Encrypted Registration Packet as zip
			FileUtils.copyToFile(new ByteArrayInputStream(packet),
					new File(filePath.concat(RegistrationConstants.ZIP_FILE_EXTENSION)));

			// LOGGER.info(LOG_PKT_STORAGE, APPLICATION_NAME, APPLICATION_ID, "Encrypted
			// packet saved");

			return filePath.concat(RegistrationConstants.ZIP_FILE_EXTENSION);
		} catch (IOException ioException) {
			// throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(),
			// REG_IO_EXCEPTION.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			// throw new
			// RegBaseUncheckedException(RegistrationConstants.ENCRYPTED_PACKET_STORAGE,
			// runtimeException.toString());
		}
		return null;
	}
}
