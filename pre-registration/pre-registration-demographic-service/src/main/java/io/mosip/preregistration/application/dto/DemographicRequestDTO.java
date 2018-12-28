/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This DTO class is used to define the initial request parameters.
 * 
 * @author Rajath KR
 * @since 1.0.0
 *
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
public class DemographicRequestDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4966448852014107698L;

	/**
	 * Id
	 */
	String id;

	/**
	 * version
	 */
	String ver;

	/**
	 * Request Date Time
	 */
	Date reqTime;

	/**
	 * Request Object
	 */
	T request;
}
