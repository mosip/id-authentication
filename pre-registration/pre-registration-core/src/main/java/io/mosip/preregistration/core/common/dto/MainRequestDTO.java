/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.core.common.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class is used to define the initial request parameters.
 * 
 * @author Rajath KR
 * @author Akshay Jain
 * @since 1.0.0
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MainRequestDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4966448852014107698L;

	/**
	 * Id
	 */
	@ApiModelProperty(value = "request id", position = 1)
	private String id;
	/**
	 * version
	 */
	@ApiModelProperty(value = "request version", position = 2)
	private String version;
	/**
	 * Request Date Time
	 */
	
	@ApiModelProperty(value = "request time", position = 3)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") 
	private Date requesttime;
	/**
	 * Request Object
	 */
	@ApiModelProperty(value = "request", position = 4)
	private T request;

}
