package io.mosip.kernel.masterdata.dto.response;

import java.time.LocalDateTime;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Ramadurai Pandian
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationPostResponseDto {

	private String code;

	private String name;

	private short hierarchyLevel;

	private String hierarchyName;

	private String parentLocCode;

	private String langCode;

	private Boolean isActive;
	
	private String createdBy;

	private LocalDateTime createdDateTime;

	private String updatedBy;

	private LocalDateTime updatedDateTime;

	private Boolean isDeleted;

	private LocalDateTime deletedDateTime;

}
