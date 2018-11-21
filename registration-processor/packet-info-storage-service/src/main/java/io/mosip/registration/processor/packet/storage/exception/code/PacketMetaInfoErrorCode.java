package io.mosip.registration.processor.packet.storage.exception.code;

public final class PacketMetaInfoErrorCode {

	public static final String RPR_REGISTRATION_PROCESSOR_PREFIX = "RPR-";
	public static final String RPR_REGISTRATION_PROCESSOR_MODULE = "PIS-";
	public static final String ERRORCODE1 = "001";
	public static final String ERRORCODE2 = "002";
	public static final String ERRORCODE3 = "003";
	public static final String ERRORCODE4 = "004";
	public static final String ERRORCODE5 = "005";
	public static final String ERRORCODE6 = "006";

	public static final String TABLE_NOT_ACCESSIBLE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_REGISTRATION_PROCESSOR_MODULE + ERRORCODE1;

	public static final String IDENTITY_NOT_FOUND = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_REGISTRATION_PROCESSOR_MODULE + ERRORCODE2;

	public static final String UNABLE_TO_INSERT_DATA = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_REGISTRATION_PROCESSOR_MODULE + ERRORCODE3;

	public static final String FILE_NOT_FOUND = RPR_REGISTRATION_PROCESSOR_PREFIX + RPR_REGISTRATION_PROCESSOR_MODULE
			+ ERRORCODE4;

	public static final String MAPPING_JSON_EXCEPTION = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_REGISTRATION_PROCESSOR_MODULE + ERRORCODE5;

	public static final String PARSING_EXCEPTION = RPR_REGISTRATION_PROCESSOR_PREFIX + RPR_REGISTRATION_PROCESSOR_MODULE
			+ ERRORCODE6;

	private PacketMetaInfoErrorCode() {
		super();
	}

}
