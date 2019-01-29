package io.mosip.registration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreRegArchiveDTO {
	@JsonProperty("pre-registration-id")
	private String preRegistrationId;

	@JsonProperty("registration-client-id")
	private String registrationCenterId;

	@JsonProperty("appointment-date")
	private String appointmentDate;

	@JsonProperty("from-time-slot")
	private String timeSlotFrom;

	@JsonProperty("to-time-slot")
	private String timeSlotTo;

	@JsonProperty("zip-filename")
	private String fileName;

	@JsonProperty("zip-bytes")
	private byte[] zipBytes;

}
