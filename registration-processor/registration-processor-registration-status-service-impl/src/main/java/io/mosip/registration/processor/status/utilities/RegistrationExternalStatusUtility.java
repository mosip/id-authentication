package io.mosip.registration.processor.status.utilities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;

/**
 * The Class RegistrationExternalStatusUtility.
 */
@Component
public class RegistrationExternalStatusUtility {
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(RegistrationExternalStatusUtility.class);

	/** The threshold time. */
	@Value("${registration.processor.threshold}")
	private int thresholdTime;

	/** The elapsed time. */
	@Value("${registration.processor.reprocess.elapse.time}")
	private int elapsedTime;
	
	/**
	 * Instantiates a new registration external status utility.
	 */
	public RegistrationExternalStatusUtility() {
		super();
	}

	/**
	 * Gets the external status.
	 *
	 * @param entity
	 *            the entity
	 * @return the external status
	 */
	public RegistrationExternalStatusCode getExternalStatus(RegistrationStatusEntity entity) {

		RegistrationExternalStatusCode mappedValue = null;

		String status = entity.getStatusCode();
		if (status.equalsIgnoreCase(RegistrationTransactionStatusCode.PROCESSED.toString())) {
			mappedValue = RegistrationExternalStatusCode.PROCESSED;
		} else if (status.equalsIgnoreCase(RegistrationTransactionStatusCode.PROCESSING.toString())) {
			mappedValue = checkStatusforPacketReceiver(entity);
		} else if (status.equalsIgnoreCase(RegistrationTransactionStatusCode.FAILED.toString())) {
			mappedValue = checkStatusforPacketUploader(entity);
		} else {
			mappedValue = RegistrationExternalStatusCode.REJECTED;
		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				entity.getReferenceRegistrationId(), "RegistrationStatusMapUtil::getExternalStatus()::exit");
		return mappedValue;
	}

	/**
	 * Check statusfor packet receiver.
	 *
	 * @param entity
	 *            the entity
	 * @return the registration external status code
	 */
	private RegistrationExternalStatusCode checkStatusforPacketReceiver(RegistrationStatusEntity entity) {
		long timeElapsedinPacketreceiver = checkElapsedTime(entity);
		if ((entity.getLatestTransactionTypeCode()
				.equalsIgnoreCase(RegistrationTransactionTypeCode.PACKET_RECEIVER.toString()))
				&& (timeElapsedinPacketreceiver > elapsedTime)) {
			if ((entity.getRetryCount() < thresholdTime)) {
				return RegistrationExternalStatusCode.RESEND;
			}
			return RegistrationExternalStatusCode.REREGISTER;
		} else {
			return RegistrationExternalStatusCode.PROCESSING;
		}

	}

	/**
	 * Check status for packet uploader.
	 *
	 * @param entity
	 *            the entity
	 * @return the registration external status code
	 */
	private RegistrationExternalStatusCode checkStatusforPacketUploader(RegistrationStatusEntity entity) {
		if ((entity.getLatestTransactionTypeCode()
				.equalsIgnoreCase(RegistrationTransactionTypeCode.PACKET_RECEIVER.toString())
				|| entity.getLatestTransactionTypeCode()
				.equalsIgnoreCase(RegistrationTransactionTypeCode.UPLOAD_PACKET.toString()))
				&& (entity.getRetryCount() < thresholdTime)) {
			return RegistrationExternalStatusCode.RESEND;
		} else
			return RegistrationExternalStatusCode.REREGISTER;
	}

	/**
	 * Check elapsed time.
	 *
	 * @param entity
	 *            the entity
	 * @return the long
	 */
	private Long checkElapsedTime(RegistrationStatusEntity entity) {
		LocalDateTime createdTime = entity.getLatestTransactionTimes();
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime tempDate = LocalDateTime.from(createdTime);
		long seconds = tempDate.until(currentTime, ChronoUnit.SECONDS);
		return seconds;
	}

}
