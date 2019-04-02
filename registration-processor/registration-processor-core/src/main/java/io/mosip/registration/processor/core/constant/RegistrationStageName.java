package io.mosip.registration.processor.core.constant;

/**
 * The Class RegistrationStageName.
 */
public class RegistrationStageName {

	/**
	 * Instantiates a new registration stage name.
	 */
	private RegistrationStageName() {

	}

	/** The Constant PACKET_RECEIVER_STAGE. */
	public static final String PACKET_RECEIVER_STAGE = "PacketReceiverStage";

	/** The Constant VIRUS_SCANNER_STAGE. */
	public static final String VIRUS_SCANNER_STAGE = "VirusScannerStage";

	/** The Constant PACKET_UPLOADER_STAGE. */
	public static final String PACKET_UPLOADER_STAGE = "PacketUploaderStage";

	/** The Constant PACKET_VALIDATOR_STAGE. */
	public static final String PACKET_VALIDATOR_STAGE = "PacketValidatorStage";

	/** The Constant OSI_VALIDATOR_STAGE. */
	public static final String OSI_VALIDATOR_STAGE = "OSIValidatorStage";

	/** The Constant DEMO_DEDUPE_STAGE. */
	public static final String DEMO_DEDUPE_STAGE = "DemoDedupeStage";

	/** The Constant MANUAL_VERIFICATION_STAGE. */
	public static final String MANUAL_VERIFICATION_STAGE = "ManualVerificationStage";

	/** The Constant BIO_DEDUPE_STAGE. */
	public static final String BIO_DEDUPE_STAGE = "BioDedupeStage";

	/** The Constant UIN_GENERATOR_STAGE. */
	public static final String UIN_GENERATOR_STAGE = "UinGeneratorStage";
}
