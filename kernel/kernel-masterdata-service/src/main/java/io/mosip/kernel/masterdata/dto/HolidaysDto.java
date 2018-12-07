package io.mosip.kernel.masterdata.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class HolidaysDto {
	
	
	
	
	
	@NotNull
	@Size(min = 1, max = 36)
	private String locationCode;
	
	@NotNull
	private LocalDate holidayDate;
	
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
