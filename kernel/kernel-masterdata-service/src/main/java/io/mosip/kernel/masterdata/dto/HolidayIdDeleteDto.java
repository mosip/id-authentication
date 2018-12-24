package io.mosip.kernel.masterdata.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class HolidayIdDeleteDto {
	@NotBlank
	private String locationCode;
	@NotBlank
	private LocalDate holidayDate;
	@NotBlank
	private String holidayName;
}
