/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.dto;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class is used to define the values for upload document.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MainRequestDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7070542323407937205L;
	
	/**
	 * Id
	 */
	@ApiModelProperty(value = "request id", position = 1)
	private String id;
	/**
	 * Version
	 */
	@ApiModelProperty(value = "version", position = 2)
	private String ver;
    /**
     * Request Time
     */
	@ApiModelProperty(value = "request time", position = 3)
	private Date reqTime;
    /**
     * Request object	
     */
	@ApiModelProperty(value = "request", position = 4)
	private T request;
}
