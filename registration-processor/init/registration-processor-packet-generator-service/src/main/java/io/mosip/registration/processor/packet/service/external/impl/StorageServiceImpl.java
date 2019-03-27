package io.mosip.registration.processor.packet.service.external.impl;

import static io.mosip.kernel.core.util.DateUtils.formatDate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.service.constants.RegistrationConstants;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.packet.service.external.StorageService;

/**
 * Class to store the encrypted packet and acknowledgement receipt of the
 * Registration in local disk
 * 
 * @author Sowmya
 * @since 1.0.0
 *
 */
@Service
public class StorageServiceImpl implements StorageService {

	@Value("${registration.processor.packet.storageLocation.encrypted}")
	private String packetStoreLocationEncrypted;

	@Value("${registration.processor.packet.storageLocation.decrypted}")
	private String packetStoreLocationdecrypted;

	@Autowired
	private Environment environment;

	private static Logger regProcLogger = RegProcessorLogger.getLogger(StorageServiceImpl.class);

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
			String filePath = "";
			String seperator = "/";

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

			FileUtils.copyToFile(new ByteArrayInputStream(packet),
					new File(filePath.concat(RegistrationConstants.ZIP_FILE_EXTENSION)));

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Packet saved in path");

			return filePath.concat(RegistrationConstants.ZIP_FILE_EXTENSION);
		} catch (IOException ioException) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage()
							+ ExceptionUtils.getStackTrace(ioException));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION, ioException);
		} catch (RuntimeException runtimeException) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, PlatformErrorMessages.RPR_SYS_SERVER_ERROR.getMessage()
							+ ExceptionUtils.getStackTrace(runtimeException));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_SYS_SERVER_ERROR, runtimeException);

		}

	}
}
