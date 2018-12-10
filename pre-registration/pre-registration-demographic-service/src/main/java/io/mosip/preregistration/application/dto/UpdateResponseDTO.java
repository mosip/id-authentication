package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Update Response DTO
 * 
 * @author M1046462
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

	private String status;

	private Date resTime;

	private T response;

}
