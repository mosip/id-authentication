package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new digital id dto.
 */
@Data
public class DigitalId {

	private String serialNo;
	private String make;
	private String model;
	private String type;
	private String dpId;
	private String dateTime;
	private String dp;

}
