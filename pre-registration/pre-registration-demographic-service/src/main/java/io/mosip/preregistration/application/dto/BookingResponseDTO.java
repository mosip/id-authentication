package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import io.mosip.preregistration.core.exceptions.dto.ExceptionJSONInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

	private Timestamp resTime;

	private T response;

}
