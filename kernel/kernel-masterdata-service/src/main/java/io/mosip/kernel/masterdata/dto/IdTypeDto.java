package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for IdType fetch response.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data


public class IdTypeDto {
	/**
	 * The id code.
	 */
	private String code;
	/**
	 * The id description.
	 */
	private String descr;
	/**
	 * The language code.
	 */
	private String langCode;
	
	private Boolean isActive;
}

