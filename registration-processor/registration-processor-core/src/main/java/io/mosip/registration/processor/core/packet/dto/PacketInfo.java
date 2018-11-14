package io.mosip.registration.processor.core.packet.dto;

import java.util.HashMap;

import lombok.Data;

/**
 * Instantiates a new packet info.
 */
@Data
public class PacketInfo {

	/** The photograph. */
	private Photograph photograph;
	
	/** The biometeric data. */
	private BiometericData biometericData;
	
	/** The document. */
	private Document document;
	
	/** The meta data. */
	private MetaData metaData;
	
	/** The osi data. */
	private OsiData osiData;
	
	/** The hash sequence. */
	private HashSequence hashSequence;
	
	/** The check sum map. */
	private HashMap<String, String> checkSumMap;
}
