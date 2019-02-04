package io.mosip.authentication.core.dto.spinstore;

import lombok.Data;

/**
 * This Class is used to provide Request for static pin value.
 * 
 * @author Prem Kumar
 *
 */
@Data
public class StaticPinRequestDTO {
	/** Variable to hold id */
	private String id;
	
	/** The value Version */
	private String ver;

	/** Variable to hold Request time */
	private String reqTime;
	
	/** The value tspID */
	private String tspID;
	
	/** The value PinRequestDTO */
	private PinRequestDTO request;
}
