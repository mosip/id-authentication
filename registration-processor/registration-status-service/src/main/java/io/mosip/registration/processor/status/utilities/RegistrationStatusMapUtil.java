package io.mosip.registration.processor.status.utilities;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;

public class RegistrationStatusMapUtil {
	private static EnumMap<RegistrationStatusCode, RegistrationExternalStatusCode> statusMap = new EnumMap<>(
			RegistrationStatusCode.class);

	private static Map<RegistrationStatusCode, RegistrationExternalStatusCode> unmodifiableMap = Collections
			.unmodifiableMap(statusMap);

	private RegistrationStatusMapUtil() {
		super();
	}

	public static Map<RegistrationStatusCode, RegistrationExternalStatusCode> statusMapper() {

		statusMap.put(RegistrationStatusCode.PACKET_UPLOADED_TO_LANDING_ZONE,
				RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.VIRUS_SCAN_FAILED, RegistrationExternalStatusCode.RESEND);
		statusMap.put(RegistrationStatusCode.VIRUS_SCAN_SUCCESSFUL, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.PACKET_UPLOADED_TO_FILESYSTEM, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.PACKET_DECRYPTION_SUCCESSFUL, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_DECRYPTION_FAILED, RegistrationExternalStatusCode.RESEND);

		statusMap.put(RegistrationStatusCode.STRUCTURAL_VALIDATION_SUCCESSFULL,
				RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.STRUCTURAL_VALIDATION_FAILED, RegistrationExternalStatusCode.RESEND);

		statusMap.put(RegistrationStatusCode.PACKET_DATA_STORE_SUCCESSFUL, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_DATA_STORE_FAILED, RegistrationExternalStatusCode.RESEND);

		statusMap.put(RegistrationStatusCode.PACKET_OSI_VALIDATION_SUCCESSFUL,
				RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_OSI_VALIDATION_FAILED, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.PACKET_DEMO_DEDUPE_SUCCESSFUL, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_DEMO_POTENTIAL_MATCH, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_DEMO_DEDUPE_FAILED, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.PACKET_BIO_DEDUPE_SUCCESSFUL, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_BIO_POTENTIAL_MATCH, RegistrationExternalStatusCode.PROCESSING);
		statusMap.put(RegistrationStatusCode.PACKET_BIO_DEDUPE_FAILED, RegistrationExternalStatusCode.PROCESSING);

		statusMap.put(RegistrationStatusCode.UIN_GENERATED, RegistrationExternalStatusCode.PROCESSED);

		return unmodifiableMap;

	}

}
