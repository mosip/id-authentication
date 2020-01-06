package io.mosip.registration.service.external.impl;

import static io.mosip.kernel.core.util.DateUtils.formatDate;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_STORAGE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.ZIP_FILE_EXTENSION;

import java.io.ByteArrayInputStream;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.external.StorageService;

/**
 * Implementation class of {@link StorageService} to store the encrypted packet
 * of the {@link Registration} to the configured location in local disk
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class StorageServiceImpl extends BaseService implements StorageService {

	private static final Logger LOGGER = AppConfig.getLogger(StorageServiceImpl.class);

	@Value("${mosip.reg.packetstorepath}")
	private String packetStoreLocation;

	public void setPacketStoreLocation(String packetStoreLocation) {
		this.packetStoreLocation = packetStoreLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.external.StorageService#storeToDisk(java.lang.
	 * String, byte[])
	 */
	@Override
	public String storeToDisk(final String registrationId, final byte[] packet) throws RegBaseCheckedException {
		try {

			// Validate the input parameters and required configuration parameters
			validateInputData(registrationId, packet);

			// Generate the file path for storing the Encrypted Packet
			String seperator = "/";
			String filePath = packetStoreLocation.concat(seperator)
					.concat(formatDate(new Date(),
							String.valueOf(
									ApplicationContext.map().get(RegistrationConstants.PACKET_STORE_DATE_FORMAT))))
					.concat(seperator).concat(registrationId);

			// Storing the Encrypted Registration Packet as zip
			FileUtils.copyToFile(new ByteArrayInputStream(CryptoUtil.encodeBase64(packet).getBytes()),
					FileUtils.getFile(filePath.concat(ZIP_FILE_EXTENSION)));

			LOGGER.info(LOG_PKT_STORAGE, APPLICATION_NAME, APPLICATION_ID, "Encrypted packet saved");

			return filePath;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_PACKET_STORAGE_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_STORAGE_EXCEPTION.getErrorMessage(), ioException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(
					RegistrationExceptionConstants.REG_PACKET_STORAGE_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_STORAGE_EXCEPTION.getErrorMessage(), runtimeException);
		}
	}

	private void validateInputData(final String registrationId, final byte[] packet) throws RegBaseCheckedException {
		if (isStringEmpty(registrationId)) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PACKET_STORAGE_INVALID_RID);
		}

		if (isByteArrayEmpty(packet)) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PACKET_STORAGE_INVALID_DATA);
		}

		if (ApplicationContext.map().get(RegistrationConstants.PACKET_STORE_LOCATION) == null
				|| ApplicationContext.map().get(RegistrationConstants.PACKET_STORE_LOCATION).toString().isEmpty()) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PACKET_STORAGE_LOCATION_INVALID);
		}

		if (ApplicationContext.map().get(RegistrationConstants.PACKET_STORE_DATE_FORMAT) == null
				|| ApplicationContext.map().get(RegistrationConstants.PACKET_STORE_DATE_FORMAT).toString().isEmpty()) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PACKET_STORAGE_DATE_FORMAT_INVALID);
		}
	}

}
