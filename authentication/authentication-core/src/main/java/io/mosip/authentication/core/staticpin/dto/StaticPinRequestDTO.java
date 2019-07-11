package io.mosip.authentication.core.staticpin.dto;

import lombok.Data;

/**
 * This Class is used to provide Request for static pin value.
 * 
 * @author Prem Kumar
 * @author DineshKaruppiah Thiagarajan
 *
 */
@Data
public class StaticPinRequestDTO {

	/** Variable to hold id */
	private String id;

	/** The value Version */
	private String version;

	/** Variable to hold Request time */
	private String requestTime;

	/** Variable to hold Individual Id */
	private String individualId;

	/** Variable to hold Individual Id type */
	private String individualIdType;

	/** The value PinRequestDTO */
	private PinRequestDTO request;
}
