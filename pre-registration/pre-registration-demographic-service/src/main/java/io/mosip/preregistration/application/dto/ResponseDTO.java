/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO
 * 
 * @author Rajath KR
 * @since 1.0.0
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class ResponseDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The error details. */
	private ExceptionJSONInfoDTO err;

	private String status;

	private Date resTime;

	private List<T> response;

}
