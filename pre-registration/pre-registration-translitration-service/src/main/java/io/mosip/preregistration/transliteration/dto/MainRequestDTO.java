/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.dto;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class is used to define the initial request parameters.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MainRequestDTO<T> implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6813813663376041900L;
	
	/**
	 * Id
	 */
	@ApiModelProperty(value = "request id", position = 1)
	private String id;
	
	/**
	 * version
	 */
	@ApiModelProperty(value = "version", position = 2)
	private String ver;
	
	/**
	 * Request Date Time
	 */
	@ApiModelProperty(value = "request time", position = 3)
	private Date reqTime;
	
	/**
	 * Request Object
	 */
	@ApiModelProperty(value = "request", position = 4)
	private T request;

}
