package io.mosip.kernel.masterdata.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayIDDto {

	private String locationCode;

	private LocalDate holidayDate;

	private String holidayName;

	private String langCode;
}
