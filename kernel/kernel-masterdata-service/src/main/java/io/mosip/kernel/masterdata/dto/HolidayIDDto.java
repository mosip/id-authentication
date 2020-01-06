package io.mosip.kernel.masterdata.dto;

import java.time.LocalDate;

import io.mosip.kernel.masterdata.validator.ValidLangCode;
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

	@ValidLangCode
	private String langCode;
}
