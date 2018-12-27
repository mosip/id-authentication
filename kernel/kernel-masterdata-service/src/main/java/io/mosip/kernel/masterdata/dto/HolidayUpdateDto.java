package io.mosip.kernel.masterdata.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayUpdateDto {
	@NotNull
	private int id;

	@Size(min = 1, max = 36)
	@NotBlank
	private String locationCode;

	@NotNull
	private LocalDate holidayDate;
	@NotBlank
	@Size(min = 1, max = 64)
	private String holidayName;

	@Size(min = 1, max = 128)
	@NotBlank
	private String holidayDesc;

	@Size(min = 1, max = 3)
	@NotBlank
	private String langCode;

	@NotNull
	private Boolean isActive;

	@Size(min = 1, max = 64)
	private String newHolidayName;

	private LocalDate newHolidayDate;

	@Size(min = 1, max = 128)
	private String newHolidayDesc;

}
