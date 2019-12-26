package io.mosip.kernel.masterdata.dto;


import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class WorkingNonWorkingDaysDto {
	
	@NotNull
	Boolean sun;
	@NotNull
	Boolean mon;
	@NotNull
	Boolean tue;
	@NotNull
	Boolean wed;
	@NotNull
	Boolean thu;
	@NotNull
	Boolean fri;
	@NotNull
	Boolean sat;
	

}
