package io.mosip.registration.processor.core.packet.dto;

import java.util.HashMap;

import lombok.Data;

@Data
public class PacketInfo {

	private Photograph photograph;
	private BiometericData biometericData;
	private Document document;
	private MetaData metaData;
	private OsiData osiData;
	private HashSequence hashSequence;
	private HashMap<String, String> checkSumMap;
}
