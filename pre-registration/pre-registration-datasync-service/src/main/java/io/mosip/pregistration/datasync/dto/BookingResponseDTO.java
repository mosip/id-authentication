/* 
 * Copyright
 * 
 */
package io.mosip.pregistration.datasync.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class is used to accept the response during Rest call to Booking
 * service
 * 
 * @author Jagadishwari S
 * @since 1.0.0
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BookingResponseDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The error details. */
	private ExceptionJSONInfoDTO err;

	/**
	 * Response status
	 */
	private String status;

	/**
	 * Response Date Time
	 */
	private Date resTime;

	/**
	 * Repsonse object
	 */
	private T response;

}
