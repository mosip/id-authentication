package io.mosip.preregistration.batchjob.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayDto {
	private String holidayId;
	private String holidayDate;
	/**
	 * Holiday day is day of week as integer value, week start from Monday , Monday is 1 and Sunday is 7
	 */
	private String holidayDay;
	/**
	 * Holiday month is month of the year as integer value.
	 */
	private String holidayMonth;
	private String holidayYear;
	private String holidayName;
	private String languageCode;
	private Boolean isActive;

}
