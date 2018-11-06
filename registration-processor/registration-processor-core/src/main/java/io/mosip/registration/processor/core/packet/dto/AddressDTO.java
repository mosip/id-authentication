package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

/**
 * Instantiates a new address DTO.
 */
@Data
public class AddressDTO {

	/** The line 1. */
	private String line1;

	/** The line 2. */
	private String line2;

	/** The line 3. */
	private String line3;

	/** The location DTO. */
	private LocationDTO locationDTO;

}