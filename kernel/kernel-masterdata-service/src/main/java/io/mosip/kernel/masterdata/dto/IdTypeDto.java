package io.mosip.kernel.masterdata.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for holding the idtype request.
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
	@NotNull
	@Size(min = 1, max = 36)
	private String code;
	
	/**
	 * The id description.
	 */
	@Size(min = 1, max = 128)
	private String descr;

	/**
	 * The name of the idtype.
	 */
	@NotNull
	@Size(min = 1, max = 64)
	private String name;
	
	/**
	 * The language code.
	 */
	@NotNull
	@Size(min = 1, max = 3)
	private String langCode;

	/**
	 * The idtype is active or not.
	 */
	@NotNull
	private Boolean isActive;
}
