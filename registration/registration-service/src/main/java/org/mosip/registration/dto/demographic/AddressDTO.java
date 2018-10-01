package org.mosip.registration.dto.demographic;

import lombok.Data;

@Data
public class AddressDTO {
	private String line1;
	private String line2;
	private String city;
	private String state;
	private String country;
}
