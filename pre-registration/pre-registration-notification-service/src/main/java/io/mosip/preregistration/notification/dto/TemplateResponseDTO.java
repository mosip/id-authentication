package io.mosip.preregistration.notification.dto;

import lombok.Data;

/**
 * DTO class for template response.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 */
@Data
public class TemplateResponseDTO {
	/**
	 * The id.
	 */
	private String id;

	/**
	 * The name.
	 */
	private String name;

	/**
	 * The description.
	 */
	private String description;

	/**
	 * The file format code.
	 */
	private String fileFormatCode;

	/**
	 * The model.
	 */
	private String model;

	/**
	 * The file text.
	 */
	private String fileText;

	/**
	 * The module id.
	 */
	private String moduleId;

	/**
	 * The module name.
	 */
	private String moduleName;

	/**
	 * The template type code.
	 */
	private String templateTypeCode;

	/**
	 * The language code.
	 */
	private String langCode;

	/**
	 * The boolean type.
	 */
	private Boolean isActive;

}
