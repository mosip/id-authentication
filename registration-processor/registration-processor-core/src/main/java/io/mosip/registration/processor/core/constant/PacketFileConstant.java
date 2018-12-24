package io.mosip.registration.processor.core.constant;

public class PacketFileConstant {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant DEMOGRAPHIC_APPLICANT. */
	public static final String DEMOGRAPHIC_APPLICANT = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;

	/** The Constant BIOMETRIC_APPLICANT. */
	public static final String BIOMETRIC_APPLICANT = PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;

	/** The Constant BIOMETRIC_INTRODUCER. */
	public static final String BIOMETRIC_INTRODUCER = PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR
			+ PacketFiles.INTRODUCER.name() + FILE_SEPARATOR;

	private PacketFileConstant() {

	}

}
