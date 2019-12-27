package io.mosip.kernel.masterdata.dto.postresponse;

import java.time.LocalDateTime;

import javax.persistence.Column;

import lombok.Data;
/**
 * 
 * @author Ramadurai Pandian
 * @since 1.0.0
 */

@Data
public class DocumentTypePostResponseDto {
	
	private String code;
	
	private String langCode;
	
	private String name;
	
	private String description;
	
	private Boolean isActive;
	
	private String createdBy;
	
	private LocalDateTime createdDateTime;

	private String updatedBy;

	private LocalDateTime updatedDateTime;

	private Boolean isDeleted;
	
	private LocalDateTime deletedDateTime;
	
}
