package io.mosip.kernel.masterdata.dto.postresponse;

import lombok.Data;

/**
 * DTO class for valid document.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class DocCategoryAndTypeResponseDto {

	/**
	 * The document category code.
	 */
	private String docCategoryCode;
	/**
	 * The document type code.
	 */
	private String docTypeCode;
}
