/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Update Response DTO
 * 
 * @author Ravi C Balaji
 * @since 1.0.0
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateResponseDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The error details. */
	private List<ExceptionJSONInfoDTO> err;

	/**
	 * Response status
	 */
	private String status;

	/**
	 * Response Date Time
	 */
	private Date resTime;

	/**
	 * Response Object
	 */
	private T response;

}
