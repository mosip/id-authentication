package io.mosip.registration.processor.core.util;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;

public class RegistrationStatusMapperUtil {
	/** The status map. */
	private static EnumMap<RegistrationExceptionTypeCode, RegistrationTransactionStatusCode> statusMap = new EnumMap<>(
			RegistrationExceptionTypeCode.class);

	/** The unmodifiable map. */
	private static Map<RegistrationExceptionTypeCode, RegistrationTransactionStatusCode> unmodifiableMap = Collections
			.unmodifiableMap(statusMap);

	public RegistrationStatusMapperUtil() {
		super();
	}

	/**
	 * Status mapper.
	 *
	 * @return the map
	 */
	private static Map<RegistrationExceptionTypeCode, RegistrationTransactionStatusCode> statusMapper() {

		statusMap.put(RegistrationExceptionTypeCode.TABLE_NOT_ACCESSIBLE_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.INTERNAL_SERVER_ERROR, RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_NOT_FOUND_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.DATA_ACCESS_EXCEPTION, RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.IOEXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.VIRUS_SCAN_FAILED_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_DECRYPTION_FAILURE_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);

		/////////////////

		statusMap.put(RegistrationExceptionTypeCode.JSON_PARSE_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.JSON_MAPPING_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.JSON_PROCESSING_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.JSON_IO_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.JSON_SCHEMA_IO_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.FILE_IO_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.FILE_NOT_FOUND_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.FILE_NOT_FOUND_IN_DESTINATION_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.NO_SUCH_ALGORITHM_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.FILE_IO_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_GENERATOR_VALIDATION_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.RUN_TIME_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.MOSIP_INVALID_DATA_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.MOSIP_INVALID_KEY_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.REG_BASE_CHECKED_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.REG_PRINT_APP_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.CLASS_NOT_FOUND_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.QR_CODE_GENERATION_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.UIN_NOT_FOUND_IN_DATABASE, RegistrationTransactionStatusCode.ERROR);

		statusMap.put(RegistrationExceptionTypeCode.PDF_GENERATOR_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.REG_STATUS_APP_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.REG_STATUS_VALIDATION_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);

		////////
		statusMap.put(RegistrationExceptionTypeCode.EMAIL_ID_NOT_FOUND_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.PHONE_NUMBER_NOT_FOUND_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.TEMPLATE_GENERATION_FAILED_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		//////
		statusMap.put(RegistrationExceptionTypeCode.CONFIGURATION_NOT_FOUND_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.BASE_UNCHECKED_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.IDENTITY_NOT_FOUND_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);

		statusMap.put(RegistrationExceptionTypeCode.QUEUE_CONNECTION_NOT_FOUND,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.CONNECTION_UNAVAILABLE_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);

		return unmodifiableMap;

	}

	public RegistrationTransactionStatusCode getExternalStatus(String statusCode, Integer retryCount) {
		RegistrationTransactionStatusCode mappedValue;
		Map<RegistrationExceptionTypeCode, RegistrationTransactionStatusCode> mapStatus = RegistrationStatusMapperUtil
				.statusMapper();
		mappedValue = mapStatus.get(RegistrationExceptionTypeCode.valueOf(statusCode));

		return mappedValue;
	}
}
