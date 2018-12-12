package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * DTO class for RegistrationCenterType request.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
public class RegistrationCenterTypeRequestDto {
	/**
	 * the id.
	 */
	private String id;
	/**
	 * the version.
	 */
	private String ver;
	/**
	 * the timestamp.
	 */
	private LocalDateTime timestamp;
	/**
	 * the request object that holds the list of registration center types to be
	 * added.
	 */
	private RegistrationCenterTypeRequest request;
}
