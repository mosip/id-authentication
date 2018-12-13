package io.mosip.kernel.syncdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Response Dto for Template details
 * 
 * @author Neha
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDto extends BaseDto{

	private String id;

	private String name;

	private String description;

	private String fileFormatCode;

	private String model;

	private String fileText;

	private String moduleId;

	private String moduleName;

	private String templateTypeCode;

	private String langCode;
	
	private Boolean isActive;
}
