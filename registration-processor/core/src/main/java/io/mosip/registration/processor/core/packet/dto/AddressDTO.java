package io.mosip.registration.processor.core.packet.dto;

import lombok.Data;

@Data
public class AddressDTO {

	private String line1;

	private String line2;

	private String line3;

	private LocationDTO locationDTO;

}