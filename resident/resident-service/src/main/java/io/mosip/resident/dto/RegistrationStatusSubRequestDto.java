package io.mosip.resident.dto;

import lombok.Data;

@Data
public class RegistrationStatusSubRequestDto {
	public RegistrationStatusSubRequestDto(String registrationId) {
		super();
		this.registrationId = registrationId;
	}

	private String registrationId;
}
