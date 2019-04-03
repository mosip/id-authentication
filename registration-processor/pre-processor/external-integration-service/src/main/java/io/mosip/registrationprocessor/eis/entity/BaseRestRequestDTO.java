package io.mosip.registrationprocessor.eis.entity;


import java.io.Serializable;

import lombok.Data;

/**
 * base request class
 *
 */
@Data
public class BaseRestRequestDTO implements Serializable{
	
	private static final long serialVersionUID = 4373201325809902206L;

	/** The id. */
	private String id;
	
	/** The ver. */
	private String version;
	
	/** The timestamp. */
	private String requesttime;

}