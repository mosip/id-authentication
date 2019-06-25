package io.mosip.kernel.masterdata.dto;

import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
import lombok.Data;

/**
 * This request DTO for update Registration center by Admin
 * 
 * @author Megha Tanga
 * 
 * 
 *
 */

@Data
public class RegistrationCenterPutReqAdmDto {
	
	@NotBlank
	@Size(min = 1, max = 10)
	private String id;

	@NotBlank
	@Size(min = 1, max = 128)
	private String name;

	@NotBlank
	@Size(min = 1, max = 36)
	private String centerTypeCode;

	@NotBlank
	@Size(min = 1, max = 256)
	private String addressLine1;

	@Size(min = 1, max = 256)
	private String addressLine2;

	@Size(min = 1, max = 256)
	private String addressLine3;

	@NotBlank
	@Size(min = 1, max = 32)
	private String latitude;

	@NotBlank
	@Size(min = 1, max = 32)
	private String longitude;

	@NotBlank
	@Size(min = 1, max = 36)
	private String locationCode;

	@NotBlank
	@Size(min = 1, max = 36)
	private String holidayLocationCode;

	@Size(min = 1, max = 16)
	private String contactPhone;

	@NotBlank
	@Size(min = 1, max = 32)
	private String workingHours;

	@ValidLangCode
	private String langCode;

	//private Short numberOfKiosks;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime perKioskProcessTime;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime centerStartTime;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime centerEndTime;

	@Size(min = 1, max = 64)
	private String timeZone;

	@Size(min = 1, max = 128)
	private String contactPerson;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime lunchStartTime;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime lunchEndTime;

	@NotNull
	private Boolean isActive;

}

