package io.mosip.kernel.syncdata.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LocationDto extends BaseDto{

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
