package io.mosip.registration.processor.core.util;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.status.code.RegistrationTransactionTypeCode;

public class RegistrationStatusMapperUtil {
	/** The status map. */
	private static EnumMap<RegistrationExceptionTypeCode, RegistrationTransactionTypeCode> statusMap = new EnumMap<>(
			RegistrationExceptionTypeCode.class);

	/** The unmodifiable map. */
	private static Map<RegistrationExceptionTypeCode, RegistrationTransactionTypeCode> unmodifiableMap = Collections
			.unmodifiableMap(statusMap);

	public RegistrationStatusMapperUtil() {
		super();
	}

	/**
	 * Status mapper.
	 *
	 * @return the map
	 */
	private static Map<RegistrationExceptionTypeCode, RegistrationTransactionTypeCode> statusMapper() {

		statusMap.put(RegistrationExceptionTypeCode.TABLE_NOT_ACCESSIBLE_EXCEPTION,
				RegistrationTransactionTypeCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.INTERNAL_SERVER_ERROR, RegistrationTransactionTypeCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.PACKET_NOT_FOUND_EXCEPTION,
				RegistrationTransactionTypeCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.DATA_ACCESS_EXCEPTION, RegistrationTransactionTypeCode.REPROCESS);
		statusMap.put(RegistrationExceptionTypeCode.IOEXCEPTION, RegistrationTransactionTypeCode.ERROR);

		return unmodifiableMap;

	}

	public RegistrationTransactionTypeCode getExternalStatus(String statusCode, Integer retryCount) {
		RegistrationTransactionTypeCode mappedValue;
		Map<RegistrationExceptionTypeCode, RegistrationTransactionTypeCode> mapStatus = RegistrationStatusMapperUtil
				.statusMapper();
		mappedValue = mapStatus.get(RegistrationExceptionTypeCode.valueOf(statusCode));

		return mappedValue;
	}
}
