package io.mosip.kernel.masterdata.dto.getresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingDaysDto {
	
	private String name;
	
	private String dayCode;
	
	private String languagecode;
	
	private boolean isActive;
	

}
