package io.mosip.kernel.syncdata.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class that holds the variables of each registration center type list data to
 * be added.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationCenterTypeDto extends BaseDto{
	/**
	 * the code.
	 */
	private String code;
	/**
	 * the language code.
	 */
	private String langCode;
	/**
	 * the name.
	 */
	private String name;
	/**
	 * the description.
	 */
	private String descr;
	
	private Boolean isActive;
}
