package io.mosip.registration.processor.core.util;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;

/**
 * The Class RegistrationExceptionMapperUtil.
 */
public class RegistrationExceptionMapperUtil {
	/** The status map. */
	private static EnumMap<RegistrationExceptionTypeCode, RegistrationTransactionStatusCode> statusMap = new EnumMap<>(
			RegistrationExceptionTypeCode.class);

	/** The unmodifiable map. */
	private static Map<RegistrationExceptionTypeCode, RegistrationTransactionStatusCode> unmodifiableMap = Collections
			.unmodifiableMap(statusMap);

	/**
	 * Instantiates a new registration exception mapper util.
	 */
	public RegistrationExceptionMapperUtil() {
		super();
	}

	/**
	 * Status mapper.
	 *
	 * @return the map
	 */
	private static Map<RegistrationExceptionTypeCode, RegistrationTransactionStatusCode> statusMapper() {

		statusMap.put(RegistrationExceptionTypeCode.REG_STATUS_VALIDATION_EXCEPTION,
				RegistrationTransactionStatusCode.FAILED);

		statusMap.put(RegistrationExceptionTypeCode.IOEXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.VIRUS_SCAN_FAILED_EXCEPTION,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.VIRUS_SCANNER_SERVICE_FAILED,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_DECRYPTION_FAILURE_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.DUPLICATE_UPLOAD_REQUEST_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.INVALID_KEY_SPEC_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.INVALID_ID_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.ILLEGAL_ARGUMENT_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.INSTANTIATION_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.ILLEGAL_ACCESS_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.NO_SUCH_FIELD_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.SECURITY_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.JSON_PARSE_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.JSON_MAPPING_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.JSON_PROCESSING_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.JSON_IO_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.JSON_SCHEMA_IO_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.FILE_IO_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.FILE_NOT_FOUND_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.NO_SUCH_ALGORITHM_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.MOSIP_INVALID_DATA_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.MOSIP_INVALID_KEY_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.IDENTITY_NOT_FOUND_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.FILE_NOT_FOUND_IN_DESTINATION_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.PARSE_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.UNSUPPORTED_ENCODING_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.CLASS_NOT_FOUND_EXCEPTION, RegistrationTransactionStatusCode.ERROR);

		statusMap.put(RegistrationExceptionTypeCode.INVOCATION_TARGET_EXCEPTION,
				RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.INTROSPECTION_EXCEPTION, RegistrationTransactionStatusCode.ERROR);

		statusMap.put(RegistrationExceptionTypeCode.TABLE_NOT_ACCESSIBLE_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.INTERNAL_SERVER_ERROR, RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_NOT_FOUND_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.DATA_ACCESS_EXCEPTION, RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.DATA_ACCESS_LAYER_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.FSADAPTER_EXCEPTION, RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.INTERRUPTED_EXCEPTION, RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.EXECUTION_EXCEPTION, RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.UNKNOWN_HOST_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.APIS_RESOURCE_ACCESS_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.QUEUE_CONNECTION_NOT_FOUND,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.CONNECTION_UNAVAILABLE_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.RUN_TIME_EXCEPTION, RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.CONFIGURATION_NOT_FOUND_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.UNEXCEPTED_ERROR, RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.JSCH_CONNECTION, RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.SFTP_OPERATION_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_OSI_VALIDATION_FAILED,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_STRUCTURAL_VALIDATION_FAILED,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_UPLOADER_FAILED, RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_UIN_GENERATION_FAILED,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.BASE_UNCHECKED_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.BASE_CHECKED_EXCEPTION, RegistrationTransactionStatusCode.ERROR);
		statusMap.put(RegistrationExceptionTypeCode.EXTERNAL_INTEGRATION_FAILED,
				RegistrationTransactionStatusCode.FAILED);

		statusMap.put(RegistrationExceptionTypeCode.CBEFF_NOT_PRESENT_EXCEPTION,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.DEMO_DEDUPE_ABIS_RESPONSE_ERROR,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.OSI_FAILED_ON_HOLD_PARENT_PACKET,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.PARENT_UIN_AND_RID_NOT_IN_PACKET,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.PARENT_UIN_NOT_AVAIALBLE, RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.OSI_FAILED_REJECTED_PARENT,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.SUPERVISORID_AND_OFFICERID_NOT_PRESENT_IN_PACKET,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_CREATION_DATE_NOT_PRESENT_IN_PACKET,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.SUPERVISOR_OR_OFFICER_WAS_INACTIVE,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.PARENT_BIOMETRIC_NOT_IN_PACKET,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.OFFICER_BIOMETRIC_NOT_IN_PACKET,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.SUPERVISOR_BIOMETRIC_NOT_IN_PACKET,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.AUTH_ERROR, RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.AUTH_FAILED, RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.IDA_AUTHENTICATION_FAILURE,
				RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.PASSWORD_OTP_FAILURE, RegistrationTransactionStatusCode.FAILED);
		statusMap.put(RegistrationExceptionTypeCode.BIOMETRIC_EXCEPTION, RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_UIN_GENERATION_ID_REPO_ERROR,
				RegistrationTransactionStatusCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.BIOMETRIC_TYPE_EXCEPTION,
				RegistrationTransactionStatusCode.REPROCESS);
		return unmodifiableMap;

	}

	/**
	 * Gets the status code.
	 *
	 * @param exceptionCode
	 *            the exception code
	 * @return the status code
	 */
	public String getStatusCode(RegistrationExceptionTypeCode exceptionCode) {
		Map<RegistrationExceptionTypeCode, RegistrationTransactionStatusCode> mapStatus = RegistrationExceptionMapperUtil
				.statusMapper();

		return mapStatus.get(RegistrationExceptionTypeCode.valueOf(exceptionCode.toString())).toString();
	}
}
