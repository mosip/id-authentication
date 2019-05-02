package io.mosip.preregistration.datasync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


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

	public String getPreRegistrationId() {
		return preRegistrationId;
	}

	public void setPreRegistrationId(String preRegistrationId) {
		this.preRegistrationId = preRegistrationId;
	}

	public String getRegistrationCenterId() {
		return registrationCenterId;
	}

	public void setRegistrationCenterId(String registrationCenterId) {
		this.registrationCenterId = registrationCenterId;
	}

	public String getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(String appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getTimeSlotFrom() {
		return timeSlotFrom;
	}

	public void setTimeSlotFrom(String timeSlotFrom) {
		this.timeSlotFrom = timeSlotFrom;
	}

	public String getTimeSlotTo() {
		return timeSlotTo;
	}

	public void setTimeSlotTo(String timeSlotTo) {
		this.timeSlotTo = timeSlotTo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getZipBytes() {
		return zipBytes;
	}

	public void setZipBytes(byte[] zipBytes) {
		this.zipBytes = zipBytes.clone() ;
	}
	
	
}
