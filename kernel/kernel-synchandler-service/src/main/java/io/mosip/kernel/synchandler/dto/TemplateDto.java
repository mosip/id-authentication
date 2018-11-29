package io.mosip.kernel.synchandler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response Dto for Template details
 * 
 * @author Neha
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDto {

	private String id;

	private String name;

	private String description;

	private String fileFormatCode;

	private String model;

	private String fileText;

	private String moduleId;

	private String moduleName;

	private String templateTypeCode;

	private String languageCode;
	
	private Boolean isActive;
}
