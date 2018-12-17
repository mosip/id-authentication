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
 * @author M1037717
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
