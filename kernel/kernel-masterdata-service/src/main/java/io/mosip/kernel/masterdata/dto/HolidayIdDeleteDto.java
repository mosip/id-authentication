package io.mosip.kernel.masterdata.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;

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
public class HolidayIdDeleteDto {
	@NotBlank
	private String locationCode;
	@NotBlank
	private LocalDate holidayDate;
	@NotBlank
	private String holidayName;
}
