package io.mosip.kernel.masterdata.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for IdType.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdTypeDto {
	/**
	 * the id code.
	 */
	private String code;
	/**
	 * the id description.
	 */
	private String descr;
	/**
	 * the language code.
	 */
	private String langCode;
}
