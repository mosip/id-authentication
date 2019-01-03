package io.mosip.kernel.masterdata.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * DTO class for Holiday Data
 * 
 * @author Abhishek Kumar
 * @author Sidhant Agarwal
 * @since 1.0.0
 */
@Data
public class HolidayDto {

	@NotBlank
	private int id;

	@Size(min = 1, max = 36)
	@NotBlank
	private String locationCode;

	@NotBlank
	private LocalDate holidayDate;
	/**
	 * Holiday day is day of week as integer value, week start from Monday , Monday
	 * is 1 and Sunday is 7
	 */
	private String holidayDay;
	/**
	 * Holiday month is month of the year as integer value.
	 */
	private String holidayMonth;
	private String holidayYear;

	@NotBlank
	@Size(min = 1, max = 64)
	private String holidayName;

	@Size(min = 1, max = 128)
	@NotBlank
	private String holidayDesc;

	@Size(min = 1, max = 3)
	@NotBlank
	private String langCode;

	@NotBlank
	private Boolean isActive;

}
