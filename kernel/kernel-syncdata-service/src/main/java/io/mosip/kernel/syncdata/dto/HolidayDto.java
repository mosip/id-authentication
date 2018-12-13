package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class HolidayDto extends BaseDto{
	private String holidayId;
	private String holidayDate;
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
	private String holidayName;
	private String languageCode;
	private String locationCode;
	private Boolean isActive;

}
