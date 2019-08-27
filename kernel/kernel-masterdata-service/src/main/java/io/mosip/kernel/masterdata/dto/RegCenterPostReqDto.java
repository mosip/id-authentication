package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * This request DTO for create Registration center by Admin
 * 
 * @author Megha Tanga
 * 
 * 
 *
 */

@Data
public class RegCenterPostReqDto extends RegcenterBaseDto {
	
	@NotBlank
	@Size(min = 1, max = 128)
	private String name;

	@NotBlank
	@Size(min = 1, max = 256)
	private String addressLine1;

	@Size(min = 0, max = 256)
	private String addressLine2;

	@Size(min = 0, max = 256)
	private String addressLine3;

	@NotBlank
	@Size(min=3)
	private String langCode;

	@Size(min = 0, max = 128)
	private String contactPerson;

}
