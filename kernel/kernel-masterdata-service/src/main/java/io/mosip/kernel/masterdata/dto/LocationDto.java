package io.mosip.kernel.masterdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * This DTO class handles 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
public class LocationDto {

	private String locationCode;

	private String locationName;

	private int hierarchyLevel;

	private String hierarchyName;

	private String parentLocationCode;

	private String languageCode;

	private Boolean isActive;

	private String createdBy;

	private String updatedBy;
	
	
	

}
