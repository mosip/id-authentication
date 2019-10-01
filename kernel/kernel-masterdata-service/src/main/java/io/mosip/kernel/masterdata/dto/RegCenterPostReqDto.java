package io.mosip.kernel.masterdata.dto;

import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * This request DTO for create Registration center by Admin
 * 
 * @author Megha Tanga
 * 
 * 
 *
 */

@Data
public class RegCenterPostReqDto {
	
	@NotBlank
	@Size(min = 0, max = 128)
	private String name;

	@NotBlank
	@Size(min = 0, max = 256)
	private String addressLine1;

	@Size(min = 0, max = 256)
	private String addressLine2;

	@Size(min = 0, max = 256)
	private String addressLine3;

	@NotBlank
	@Size(min=3)
	private String langCode;

	@Size(min = 0, max = 128)
	private String contactPerson;
	
	@NotNull
	private Boolean isActive;
	
	private String id;
	
	@NotBlank
	@Size(min = 0, max = 36)
	private String centerTypeCode;

	@NotBlank
	@Size(min = 0, max = 32)
	private String latitude;

	@NotBlank
	@Size(min = 0, max = 32)
	private String longitude;

	@NotBlank
	@Size(min = 0, max = 36)
	private String locationCode;

	@NotBlank
	@Size(min = 0, max = 36)
	private String holidayLocationCode;

	@Size(min = 0, max = 16)
	private String contactPhone;

	@NotBlank
	@Size(min = 0, max = 32)
	private String workingHours;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime perKioskProcessTime;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime centerStartTime;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime centerEndTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime lunchStartTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime lunchEndTime;

	@Size(min = 0, max = 64)
	private String timeZone;

	@NotNull
	@Size(min = 0, max = 36)
	private String zoneCode;
}
