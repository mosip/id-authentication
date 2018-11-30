package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidDataDto {
	
	private String docTypeCode;
	
	private String docCategoryCode;
	
	private String langCode;
	
	private Boolean isActive;
}
