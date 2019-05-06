package io.mosip.registration.processor.status.utilities;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
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
	private static Map<String, RegistrationExternalStatusCode> statusMap = new HashMap<String, RegistrationExternalStatusCode>();

	/** The unmodifiable map. */
	private static Map<String, RegistrationExternalStatusCode> unmodifiableMap = Collections
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
	private static Map<String, RegistrationExternalStatusCode> statusMapper() {

		statusMap.put(RegistrationTransactionTypeCode.VIRUS_SCAN+"_"+RegistrationTransactionStatusCode.IN_PROGRESS, RegistrationExternalStatusCode.RECEIVED);

		statusMap.put(RegistrationTransactionTypeCode.VIRUS_SCAN+"_"+RegistrationTransactionStatusCode.FAILED, RegistrationExternalStatusCode.REREGISTER);
		statusMap.put(RegistrationTransactionTypeCode.VIRUS_SCAN+"_"+RegistrationTransactionStatusCode.SUCCESS, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationTransactionTypeCode.UPLOAD_PACKET+"_"+RegistrationTransactionStatusCode.SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationTransactionTypeCode.UPLOAD_PACKET+"_"+RegistrationTransactionStatusCode.FAILED,
				RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationTransactionTypeCode.VALIDATE_PACKET+"_"+RegistrationTransactionStatusCode.SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationTransactionTypeCode.VALIDATE_PACKET+"_"+RegistrationTransactionStatusCode.FAILED, RegistrationExternalStatusCode.REREGISTER);
		
		statusMap.put(RegistrationTransactionTypeCode.OSI_VALIDATE+"_"+RegistrationTransactionStatusCode.SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationTransactionTypeCode.OSI_VALIDATE+"_"+RegistrationTransactionStatusCode.FAILED, RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationTransactionTypeCode.DEMOGRAPHIC_VERIFICATION+"_"+RegistrationTransactionStatusCode.SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationTransactionTypeCode.DEMOGRAPHIC_VERIFICATION+"_"+RegistrationTransactionStatusCode.FAILED, RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationTransactionTypeCode.BIOGRAPHIC_VERIFICATION+"_"+RegistrationTransactionStatusCode.SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationTransactionTypeCode.BIOGRAPHIC_VERIFICATION+"_"+RegistrationTransactionStatusCode.FAILED, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationTransactionTypeCode.MANUAL_VARIFICATION+"_"+RegistrationTransactionStatusCode.SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationTransactionTypeCode.MANUAL_VARIFICATION+"_"+RegistrationTransactionStatusCode.FAILED, RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationTransactionTypeCode.UIN_GENERATOR+"_"+RegistrationTransactionStatusCode.SUCCESS, RegistrationExternalStatusCode.PROCESSED);
		statusMap.put(RegistrationTransactionTypeCode.UIN_GENERATOR+"_"+RegistrationTransactionStatusCode.FAILED, RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationTransactionTypeCode.PRINT+"_"+RegistrationTransactionStatusCode.SUCCESS, RegistrationExternalStatusCode.PROCESSED);
		statusMap.put(RegistrationTransactionTypeCode.NOTIFICATION+"_"+RegistrationTransactionStatusCode.SUCCESS, RegistrationExternalStatusCode.PROCESSED);
		
		
		statusMap.put(RegistrationTransactionTypeCode.EXTERNAL_INTEGRATION+"_"+RegistrationTransactionStatusCode.SUCCESS, RegistrationExternalStatusCode.PROCESSING);

		return unmodifiableMap;

	}

	public RegistrationExternalStatusCode getExternalStatus(RegistrationStatusEntity entity) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				entity.getReferenceRegistrationId(), "RegistrationStatusMapUtil::getExternalStatus()::entry");

		RegistrationExternalStatusCode mappedValue = null;
		Map<String, RegistrationExternalStatusCode> mapStatus = RegistrationStatusMapUtil
				.statusMapper();
		if (entity.getStatusCode() != null) {
			mappedValue = mapStatus.get(RegistrationStatusCode.valueOf(entity.getLatestTransactionTypeCode()+"_"+entity.getLatestTransactionStatusCode()));
			if ((entity.getLatestTransactionTypeCode()+"_"+entity.getLatestTransactionStatusCode()).equals(RegistrationTransactionTypeCode.UPLOAD_PACKET.toString()+"_"+RegistrationTransactionStatusCode.FAILED.toString()) 
					&&(entity.getRetryCount() < threshold)
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
