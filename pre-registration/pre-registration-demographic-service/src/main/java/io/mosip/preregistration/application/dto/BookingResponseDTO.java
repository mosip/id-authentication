package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.util.Date;

import io.mosip.preregistration.core.exceptions.dto.ExceptionJSONInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author M1046129
 *
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BookingResponseDTO<T> implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The error details. */
	private ExceptionJSONInfo err;

	private Boolean status;

	private Date resTime;

	private T response;

}
