package io.mosip.kernel.synchandler.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * DTO class for holding the idtype request.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class IdTypeRequest implements Serializable {

	/**
	 * Serializable version id.
	 */
	private static final long serialVersionUID = 5474187315520185266L;
	
	/**
	 * The code of id type.
	 */
	private String code;
	
	/**
	 * The language code of id type.
	 */
	private String langCode;
	
	/**
	 * The name of id type.
	 */
	private String name;
	
	/**
	 * The description of id type.
	 */
	private String descr;

}
