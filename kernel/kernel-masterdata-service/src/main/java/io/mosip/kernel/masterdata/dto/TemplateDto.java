package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

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

	private boolean isActive;

	private String createdBy;

	private LocalDateTime createdTimestamp;

	private String updatedBy;

	private LocalDateTime updatedTimestamp;

	private boolean isDeleted;

	private LocalDateTime deletedTimestamp;
}
