package io.mosip.preregistration.datasync.dto;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreRegArchiveDTO {
	@JsonProperty("pre-registration-id")
	@ApiModelProperty(value = "Pre-Registration-ID", position = 1)
	private String preRegistrationId;
	
	@JsonProperty("registration-client-id")
	@ApiModelProperty(value = "Registration-Client-Id", position = 2)
	private String registrationCenterId;
	
	@JsonProperty("appointment-date")
	@ApiModelProperty(value = "Appointment-Date", position = 3)
	private String appointmentDate;
	
	@JsonProperty("from-time-slot")
	@ApiModelProperty(value = "from-time-slot", position = 4)
	private String timeSlotFrom;
	
	@JsonProperty("to-time-slot")
	@ApiModelProperty(value = "to-time-slot", position = 5)
	private String timeSlotTo;
	
	@JsonProperty("zip-filename")
	@ApiModelProperty(value = "zip-filename", position = 6)
	private String fileName;
	
	@JsonProperty("zip-bytes")
	@ApiModelProperty(value = "zip-bytes", position = 7)
	private byte[] zipBytes;
	
}
