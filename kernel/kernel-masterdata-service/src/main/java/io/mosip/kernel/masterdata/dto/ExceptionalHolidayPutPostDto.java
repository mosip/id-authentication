
package io.mosip.kernel.masterdata.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ExceptionalHolidayPutPostDto {
	
 private LocalDate exceptionHolidayDate;
	
	private String exceptionHolidayName;
	
	private String exceptionHolidayReson;

}
