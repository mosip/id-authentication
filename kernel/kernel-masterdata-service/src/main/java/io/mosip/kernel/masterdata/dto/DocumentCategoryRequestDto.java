package io.mosip.kernel.masterdata.dto;

import lombok.Data;

/**
 * The request dto for document category.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class DocumentCategoryRequestDto {
	/**
	 * 
	 */
	private String id;
	/**
	 * 
	 */
	private String ver;
	/**
	 * 
	 */
	private String timestamp;
	/**
	 * 
	 */
	private DocumentCategoryListDto request;
}
