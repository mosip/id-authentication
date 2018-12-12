package io.mosip.kernel.masterdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
public class LocationDto {

	private String code;

	private String name;

	private int hierarchyLevel;

	private String hierarchyName;

	private String parentLocCode;

	private String languageCode;

	private Boolean isActive;

	private String createdBy;

	private String updatedBy;
	
	
	

}
