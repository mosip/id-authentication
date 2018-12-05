package io.mosip.kernel.synchandler.dto;

import lombok.Data;

/**
 * Class that holds the variables of each registration center type list data to
 * be added.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class RegistrationCenterType {
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
