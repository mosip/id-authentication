package io.mosip.registration.processor.core.exception.util;

/**
 *
 * @author M1048399 Horteppa
 * 
 */
public enum PlatformSuccessMessages {

	// Packet Receiver Success messages.

	/** The rpr pkr packet receiver. */
	PACKET_RECEIVER_VALIDATION_SUCCESS(PlatformConstants.RPR_PACKET_RECEIVER_MODULE + "001",
			"Packet receiver validation success"),

	/** The rpr pkr packet receiver. */
	RPR_PKR_PACKET_RECEIVER(PlatformConstants.RPR_PACKET_RECEIVER_MODULE + "000",
			"Packet received and uploaded to landing zone"),

	RPR_PUM_PACKET_UPLOADER(PlatformConstants.RPR_PACKET_UPLOADER_MODULE + "000", "Packet uploaded to file system"),

	// Packet Validator Success messages
	/** The rpr pkr packet validate. */
	RPR_PKR_PACKET_VALIDATE(PlatformConstants.RPR_PACKET_VALIDATOR_MODULE + "000", "Packet Validation Success"),

	// OSI validator Success messages
	/** The rpr pkr osi validate. */
	RPR_PKR_OSI_VALIDATE(PlatformConstants.RPR_OSI_VALIDATOR_MODULE + "000", "OSI Validation Success"),

	// DEMO-De-dupe Success Messages
	/** The rpr pkr demode-dupe validate. */
	RPR_PKR_DEMO_DE_DUP(PlatformConstants.RPR_DEMO_DEDUPE_MODULE + "000", "Demo-de-dupe Success"),

	RPR_PKR_DEMO_DE_DUP_POTENTIAL_DUPLICATION_FOUND(PlatformConstants.RPR_DEMO_DEDUPE_MODULE + "001",
			"Potential duplicate packet found for registration id : "),

	RPR_PKR_DEMO_DE_DUP_SKIP(PlatformConstants.RPR_DEMO_DEDUPE_MODULE + "002", "Demographic Deduplication Skipped"),

	// Biometric Authentication Success Messages
	RPR_PKR_BIOMETRIC_AUTHENTICATION(PlatformConstants.RPR_BIOMETRIC_AUTHENTICATION_MODULE + "000",
			"Biometric Authentication Success"),

	// Bio-De-dupe Success messages
	/** The Constant PACKET_BIODEDUPE_SUCCESS. */
	RPR_BIO_DEDUPE_SUCCESS(PlatformConstants.RPR_BIO_DEDUPE_SERVICE_MODULE + "000", "Packet biodedupe successful"),

	/** The Constant PACKET_BIOMETRIC_POTENTIAL_MATCH. */
	RPR_BIO_METRIC_POTENTIAL_MATCH(PlatformConstants.RPR_BIO_DEDUPE_SERVICE_MODULE + "000",
			"Potential match found while processing bio dedupe"),

	RPR_RE_PROCESS_SUCCESS(PlatformConstants.RPR_REPROCESSOR_STAGE + "000", "Reprocessor Success"),

	RPR_RE_PROCESS_FAILED(PlatformConstants.RPR_REPROCESSOR_STAGE + "002", "Reprocessor FAILED"),

	RPR_SENT_TO_REPROCESS_SUCCESS(PlatformConstants.RPR_REPROCESSOR_STAGE + "001", "sent to reprocess Success"),

	RPR_EXTERNAL_STAGE_SUCCESS(PlatformConstants.RPR_EXTERNAL_STAGE + "000", "External stage  Success"),

	RPR_UIN_GENERATOR_STAGE_SUCCESS(PlatformConstants.RPR_UIN_GENERATOR_STAGE + "000", "UIN Generator  Success"),

	RPR_UIN_DATA_UPDATION_SUCCESS(PlatformConstants.RPR_UIN_GENERATOR_STAGE + "001", "UIN Generator  Success"),

	RPR_UIN_ACTIVATED_SUCCESS(PlatformConstants.RPR_UIN_GENERATOR_STAGE + "002", "UIN Generator  Success"),

	RPR_UIN_DEACTIVATION_SUCCESS(PlatformConstants.RPR_UIN_GENERATOR_STAGE + "003", "UIN Generator  Success"),

	RPR_LINK_RID_FOR_LOST_PACKET_SUCCESS(PlatformConstants.RPR_UIN_GENERATOR_STAGE + "004", "UIN Generator  Success"),

	RPR_QUALITY_CHECK_SUCCESS(PlatformConstants.RPR_QUALITY_CHECKER_MODULE + "000", "Quality check  Success"),

	RPR_PRINT_STAGE_SENT_QUEUE_SUCCESS(PlatformConstants.RPR_PRINTING_MODULE + "000",
			"Pdf added to the mosip queue for printing"),

	RPR_PRINT_STAGE_SUCCESS(PlatformConstants.RPR_PRINTING_MODULE + "001", "Print and Post Completed"),

	RPR_MESSAGE_SENDER_STAGE_SUCCESS(PlatformConstants.RPR_MESSAGE_SENDER_TEMPLATE + "001",
			"Message Sender Stage success"),

	RPR_ABIS_HANDLER_STAGE_SUCCESS(PlatformConstants.RPR_ABIS_HANDLER + "000", "ABIS hanlder stage success"),

	RPR_ABIS_MIDDLEWARE_STAGE_SUCCESS(PlatformConstants.RPR_ABIS_MIDDLEWARE + "000",
			"Abis insertRequests sucessfully sent to Queue");

	/** The success message. */
	private final String successMessage;

	/** The success code. */
	private final String successCode;

	/**
	 * Instantiates a new platform success messages.
	 *
	 * @param errorCode
	 *            the error code
	 * @param errorMsg
	 *            the error msg
	 */
	private PlatformSuccessMessages(String errorCode, String errorMsg) {
		this.successCode = errorCode;
		this.successMessage = errorMsg;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return this.successMessage;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return this.successCode;
	}

}
