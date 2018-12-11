package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author Dharmesh Khandelwal
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Data
public class RegistrationCenterHistoryDto {
	@NotNull
	@Size(min = 1, max = 36)
	private String id;

	@NotNull
	@Size(min = 1, max = 128)
	private String name;

	@Size(min = 1, max = 36)
	private String centerTypeCode;

	@Size(min = 1, max = 256)
	private String addressLine1;

	@Size(min = 1, max = 256)
	private String addressLine2;

	@Size(min = 1, max = 256)
	private String addressLine3;

	@Size(min = 1, max = 32)
	private String latitude;

	@Size(min = 1, max = 32)
	private String longitude;

	@NotNull
	@Size(min = 1, max = 36)
	private String locationCode;

	@Size(min = 1, max = 36)
	private String holidayLocationCode;

	@Size(min = 1, max = 16)
	private String contactPhone;

	@Size(min = 1, max = 32)
	private String workingHours;

	@NotNull
	@Size(min = 1, max = 3)
	private String languageCode;

	private Short numberOfKiosks;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime perKioskProcessTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime centerStartTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime centerEndTime;

	@Size(min = 1, max = 64)
	private String timeZone;

	@Size(min = 1, max = 128)
	private String contactPerson;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime lunchStartTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
	private LocalTime lunchEndTime;

	@NotNull
	private Boolean isActive;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime effectDateTime;

}
