package io.mosip.registration.processor.status.utilities;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;

/**
 * The Class RegistrationStatusMapUtil.
 */
@Component
public class RegistrationStatusMapUtil {

	/** The status map. */
	private static EnumMap<RegistrationStatusCode, RegistrationExternalStatusCode> statusMap = new EnumMap<>(
			RegistrationStatusCode.class);

	/** The unmodifiable map. */
	private static Map<RegistrationStatusCode, RegistrationExternalStatusCode> unmodifiableMap = Collections
			.unmodifiableMap(statusMap);

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(RegistrationStatusMapUtil.class);

	@Value("${registration.processor.threshold}")
	private int threshold;

	/**
	 * Instantiates a new registration status map util.
	 */
	public RegistrationStatusMapUtil() {
		super();
	}

	/**
	 * Status mapper.
	 *
	 * @return the map
	 */
	private static Map<RegistrationStatusCode, RegistrationExternalStatusCode> statusMapper() {

		statusMap.put(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE, RegistrationExternalStatusCode.RECEIVED);

		statusMap.put(RegistrationStatusCode.VIRUS_SCAN_FAILED, RegistrationExternalStatusCode.REREGISTER);
		statusMap.put(RegistrationStatusCode.VIRUS_SCAN_SUCCESSFUL, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_UPLOAD_TO_PACKET_STORE_FAILED,
				RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationStatusCode.STRUCTURE_VALIDATION_SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.STRUCTURE_VALIDATION_FAILED, RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationStatusCode.PACKET_OSI_VALIDATION_SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_OSI_VALIDATION_FAILED, RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationStatusCode.DEMO_DEDUPE_SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.DEMO_DEDUPE_FAILED, RegistrationExternalStatusCode.REREGISTER);
		statusMap.put(RegistrationStatusCode.DEMO_DEDUPE_POTENTIAL_MATCH_FOUND,
				RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.PACKET_BIO_DEDUPE_SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.MANUAL_ADJUDICATION_SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.MANUAL_ADJUDICATION_FAILED, RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationStatusCode.PACKET_UIN_UPDATION_SUCCESS, RegistrationExternalStatusCode.PROCESSED);
		statusMap.put(RegistrationStatusCode.PACKET_UIN_UPDATION_FAILURE, RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationStatusCode.PRINT_AND_POST_COMPLETED, RegistrationExternalStatusCode.PROCESSED);
		statusMap.put(RegistrationStatusCode.NOTIFICATION_SENT_TO_RESIDENT, RegistrationExternalStatusCode.PROCESSED);
		statusMap.put(RegistrationStatusCode.PACKET_SENT_FOR_PRINTING, RegistrationExternalStatusCode.PROCESSED);
		statusMap.put(RegistrationStatusCode.UNABLE_TO_SENT_FOR_PRINTING, RegistrationExternalStatusCode.PROCESSED);

		return unmodifiableMap;

	}

	public RegistrationExternalStatusCode getExternalStatus(RegistrationStatusEntity entity) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				entity.getReferenceRegistrationId(), "RegistrationStatusMapUtil::getExternalStatus()::entry");

		RegistrationExternalStatusCode mappedValue = null;
		Map<RegistrationStatusCode, RegistrationExternalStatusCode> mapStatus = RegistrationStatusMapUtil
				.statusMapper();
		if (entity.getStatusCode() != null) {
			mappedValue = mapStatus.get(RegistrationStatusCode.valueOf(entity.getStatusCode()));
			if ((entity.getRetryCount() < threshold)
					&& (mappedValue.equals(RegistrationExternalStatusCode.REREGISTER))) {
				mappedValue = RegistrationExternalStatusCode.RESEND;
			}
		} else {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					entity.getReferenceRegistrationId(),
					PlatformErrorMessages.RPR_RGS_REGISTRATION_STATUS_NOT_EXIST.getMessage());
		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				entity.getReferenceRegistrationId(), "RegistrationStatusMapUtil::getExternalStatus()::exit");
		return mappedValue;
	}

}
