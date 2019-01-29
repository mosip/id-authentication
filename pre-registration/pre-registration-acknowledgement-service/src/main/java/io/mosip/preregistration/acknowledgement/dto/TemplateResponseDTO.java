package io.mosip.preregistration.acknowledgement.dto;

import lombok.Data;
@Data
public class TemplateResponseDTO {
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
