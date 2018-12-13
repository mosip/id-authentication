package io.mosip.kernel.syncdata.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PostReasonCategoryDto extends BaseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -845601642085487726L;
	
	private String code;

	private String name;

	private String description;

	private String langCode;
	
	private Boolean isActive;
	
	private Boolean isDeleted;

}
