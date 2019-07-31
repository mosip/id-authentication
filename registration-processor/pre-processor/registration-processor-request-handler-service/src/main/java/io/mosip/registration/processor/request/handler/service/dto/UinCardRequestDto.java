package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UinCardRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8099293922889142622L;

	private String centerId;
	
	private String machineId;
	
	private String reason;
	
	private String registrationType;
	
	private String id;
	
	private String idType;
	
	private String cardType;
}
