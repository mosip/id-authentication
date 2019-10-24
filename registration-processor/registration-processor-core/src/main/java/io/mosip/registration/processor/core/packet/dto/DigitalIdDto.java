package io.mosip.registration.processor.core.packet.dto;

import com.google.gson.JsonArray;

import lombok.Data;

/**
 * Instantiates a new digital id dto.
 */
@Data
public class DigitalIdDto {

	/** The serial no. */
	private String serialNo;

	/** The make. */
	private String make;

	/** The model. */
	private String model;

	/** The type. */
	private JsonArray type;

	/** The dp. */
	private String dp;

	/** The dp id. */
	private String dpId;

	/** The date time. */
	private String dateTime;

}
