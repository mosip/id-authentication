package io.mosip.kernel.masterdata.dto;

import lombok.Data;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class ValidDocumentRequestDto {
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
	private ValidDocumentData request;
}
