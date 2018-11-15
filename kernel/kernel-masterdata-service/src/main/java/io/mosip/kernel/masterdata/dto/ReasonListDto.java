package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReasonListDto {
	
	private String code;
	
	
	private String name;
	
	
	private String description;
	
	
	private String reasonCategoryCode;
	
	
	private String langCode;
	
	
	private boolean isActive;
	
	
	private boolean isDeleted;
	
	
}
