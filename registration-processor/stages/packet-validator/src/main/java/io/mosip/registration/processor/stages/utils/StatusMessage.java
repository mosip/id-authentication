package io.mosip.registration.processor.stages.utils;

public final class StatusMessage {
	
	private StatusMessage() {
		
	}

	public static final String PACKET_CHECKSUM_VALIDATION ="PACKET_CHECKSUM_VALIDATION_FAILURE";
	
	public static final String PACKET_FILES_VALIDATION ="PACKET_FILES_VALIDATION_FAILURE";
	
	public static final String PACKET_STRUCTURAL_VALIDATION ="PACKET_STRUCTURAL_VALIDATION_SUCCESS";
	
	public static final String INPUTSTREAM_NOT_READABLE = "Unable to read inputstream";
}
