package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateTypeRequestDto {
	
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
	private TemplateTypeData request;

}
