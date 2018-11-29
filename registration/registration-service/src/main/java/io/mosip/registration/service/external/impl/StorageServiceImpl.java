package io.mosip.registration.service.external.impl;

import static io.mosip.kernel.core.util.DateUtils.formatDate;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_STORAGE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.ZIP_FILE_EXTENSION;
import static io.mosip.registration.constants.RegistrationExceptions.REG_IO_EXCEPTION;
import static java.io.File.separator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.StorageService;

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

	private static final Logger LOGGER = AppConfig.getLogger(StorageServiceImpl.class);

	@Autowired
	private Environment environment;

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.StorageService#storeToDisk(java.lang.String, byte[], byte[])
	 */
	@Override
	public String storeToDisk(String registrationId, byte[] packet, byte[] ackReceipt) throws RegBaseCheckedException {
		try {
			// Generate the file path for storing the Encrypted Packet and Acknowledgement
			// Receipt
			String filePath = environment.getProperty(RegistrationConstants.PACKET_STORE_LOCATION) + separator
					+ formatDate(new Date(), environment.getProperty(RegistrationConstants.PACKET_STORE_DATE_FORMAT))
							.concat(separator).concat(registrationId);
			// Storing the Encrypted Registration Packet as zip
			FileUtils.copyToFile(new ByteArrayInputStream(packet), new File(filePath.concat(ZIP_FILE_EXTENSION)));

			LOGGER.debug(LOG_PKT_STORAGE, APPLICATION_NAME, APPLICATION_ID, "Encrypted packet saved");

			// Storing the Registration Acknowledge Receipt Image
			FileUtils.copyToFile(new ByteArrayInputStream(ackReceipt),
					new File(filePath.concat("_Ack.").concat(RegistrationConstants.IMAGE_FORMAT)));

			LOGGER.debug(LOG_PKT_STORAGE, APPLICATION_NAME, APPLICATION_ID,
					"Registration's Acknowledgement Receipt saved");

			return filePath;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), REG_IO_EXCEPTION.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.ENCRYPTED_PACKET_STORAGE,
					runtimeException.toString());
		}
	}
}
