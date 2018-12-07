package io.mosip.kernel.masterdata.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since 23-10-2016
 */
@Data
public class HolidayDto {
	
	@NotNull
	private int id;
	
	
	
	@NotNull
	@Size(min = 1, max = 36)
	private String locationCode;
	
	@NotNull
	private LocalDate holidayDate;
	/**
	 * Holiday day is day of week as integer value, week start from Monday , Monday is 1 and Sunday is 7
	 */
	private String holidayDay;
	/**
	 * Holiday month is month of the year as integer value.
	 */
	private String holidayMonth;
	private String holidayYear;
	
	@NotNull
	@Size(min = 1, max = 64)
	private String holidayName;
	
	@NotNull
	@Size(min = 1, max = 128)
	private String holidayDesc;
	
	@NotNull
	@Size(min = 1, max = 3)
	private String langCode;
	
	@NotNull
	private Boolean isActive;

}
