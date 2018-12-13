package io.mosip.kernel.masterdata.dto;

import lombok.Data;

/**
 * Response Dto for Template details
 * 
 * @author Neha
 * @since 1.0.0
 */
@Data
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

	private String langCode;
	
	private Boolean isActive;
}
