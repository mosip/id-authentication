package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;

import lombok.Data;

@Data
public class AbisCommonResponseDto implements Serializable{
	
	private static final long serialVersionUID = -7080424253600088998L;
	
	private String id;

	private String requestId;

	private String timestamp;
	
	private Integer returnValue;
	
	private Integer failureReason;

}
