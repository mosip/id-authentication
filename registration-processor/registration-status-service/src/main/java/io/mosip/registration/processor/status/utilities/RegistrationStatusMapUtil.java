package io.mosip.registration.processor.status.utilities;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;

/**
 * The Class RegistrationStatusMapUtil.
 */
public class RegistrationStatusMapUtil {

	/** The status map. */
	private static EnumMap<RegistrationStatusCode, RegistrationExternalStatusCode> statusMap = new EnumMap<>(
			RegistrationStatusCode.class);

	/** The unmodifiable map. */
	private static Map<RegistrationStatusCode, RegistrationExternalStatusCode> unmodifiableMap = Collections
			.unmodifiableMap(statusMap);

	@Value("${registration.processor.threshold}")
	private static int threshold;

	/**
	 * Instantiates a new registration status map util.
	 */
	private RegistrationStatusMapUtil() {
		super();
	}

	/**
	 * Status mapper.
	 *
	 * @return the map
	 */
	private static Map<RegistrationStatusCode, RegistrationExternalStatusCode> statusMapper() {

		statusMap.put(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE,
				RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.VIRUS_SCAN_FAILED, RegistrationExternalStatusCode.REREGISTER);
		statusMap.put(RegistrationStatusCode.VIRUS_SCAN_SUCCESS, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.PACKET_DECRYPTION_SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_DECRYPTION_FAILED, RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationStatusCode.STRUCTURE_VALIDATION_SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.STRUCTURE_VALIDATION_FAILED, RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationStatusCode.PACKET_DATA_STORE_SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_DATA_STORE_FAILED, RegistrationExternalStatusCode.REREGISTER);

		statusMap.put(RegistrationStatusCode.PACKET_OSI_VALIDATION_SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_OSI_VALIDATION_FAILED, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.PACKET_DEMO_DEDUPE_SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_DEMO_POTENTIAL_MATCH, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_DEMO_DEDUPE_FAILED, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.PACKET_BIO_DEDUPE_SUCCESS, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_BIO_POTENTIAL_MATCH, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.UIN_GENERATED, RegistrationExternalStatusCode.PROCESSED);

		return unmodifiableMap;

	}

	public static RegistrationExternalStatusCode getExternalStatus(String statusCode, int retryCount) {
		RegistrationExternalStatusCode mappedValue;
		if (retryCount < threshold) {
			mappedValue = RegistrationExternalStatusCode.RESEND;
		} else {
			Map<RegistrationStatusCode, RegistrationExternalStatusCode> statusMap = RegistrationStatusMapUtil
					.statusMapper();
			mappedValue = statusMap.get(RegistrationStatusCode.valueOf(statusCode));

		}
		return mappedValue;
	}

}
