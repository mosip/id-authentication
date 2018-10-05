package io.mosip.registration.processor.core.packet.dto;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class PacketInfo {

	private Photograph photograph;
	private BiometericData biometericData;
	private Document document;
	private MetaData metaData;
	private OSIData osiData;
	private HashSequence hashSequence;
	private List<Audit> audit;
	private HashMap<String, String> checkSumMap;
}
