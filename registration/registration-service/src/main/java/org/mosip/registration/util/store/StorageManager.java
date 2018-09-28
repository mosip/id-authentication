package org.mosip.registration.util.store;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;

import static java.io.File.separator;

import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.constants.RegProcessorExceptionCode;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.core.utils.FileUtil;

import static org.mosip.kernel.core.utils.DateUtil.formatDate;
import static org.mosip.registration.constants.RegConstants.ZIP_FILE_EXTENSION;
import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_IO_EXCEPTION;
import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME; 
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

/**
 * Class to Store the Packets in local disk
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class StorageManager {

	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Writes the encrypted packet to the local storage
	 * 
	 * @param registrationId
	 *            the id of the Registration
	 * @param packet
	 *            the encrypted packet data to be stored in local storage
	 * @param ackReceipt
	 *            the registration acknowledgement receipt to be stored in local
	 *            storage
	 * @return returns the file path where the files had been stored
	 * @throws RegBaseCheckedException
	 */
	public String storeToDisk(String registrationId, byte[] packet, byte[] ackReceipt) throws RegBaseCheckedException {
		try {
			// Generate the file path for storing the Encrypted Packet and Acknowledgement
			// Receipt
			String filePath = getPropertyValue(RegConstants.PACKET_STORE_LOCATION) + separator
					+ formatDate(new Date(), getPropertyValue(RegConstants.PACKET_STORE_DATE_FORMAT)).concat(separator)
							.concat(registrationId.replaceAll("[^0-9]", ""));
			// Storing the Encrypted Registration Packet as zip
			FileUtil.copyToFile(new ByteArrayInputStream(packet), new File(filePath.concat(ZIP_FILE_EXTENSION)));
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - LOCAL STORAGE", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Encrypted packet saved");
			// Storing the Registration Acknowledge Receipt Image
			FileUtil.copyToFile(new ByteArrayInputStream(ackReceipt), new File(filePath.concat("_Ack.").concat(RegConstants.IMAGE_FORMAT)));
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - LOCAL STORAGE", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Registration's Acknowledgement Receipt saved");
			return filePath;
		} catch (MosipIOException ioException) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), REG_IO_EXCEPTION.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.ENCRYPTED_PACKET_STORAGE,
					runtimeException.toString());
		}
	}
}
