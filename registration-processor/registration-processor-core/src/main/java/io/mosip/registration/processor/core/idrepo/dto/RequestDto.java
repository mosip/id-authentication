package io.mosip.registration.processor.core.idrepo.dto;

import java.util.List;

import lombok.Data;

/**
 * The Class RequestDto.
 *
 * @author Nagalakshmi
 */

/**
 * Instantiates a new request dto.
 */
@Data
public class RequestDto {

	/** The identity. */
	private Object identity;

	/** The documents. */
	private List<Documents> documents;

	/** The registration id. */
	private String registrationId;

	private String status;

	/** The UIN */
	private String biometricReferenceId;
}
