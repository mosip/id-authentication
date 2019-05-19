package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;

import lombok.Data;

@Data
public class AbisCommonRequestDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7080424253600088998L;

	/** The id. */
	private String id;
	
	/** The ver. */
	private String ver;
	
	/** The request id. */
	private String requestId;
	
	/** The timestamp. */
	private String timestamp;
	
	/** The reference id. */
	private String referenceId;
}
