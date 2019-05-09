package io.mosip.idrepository.vid.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.mosip.kernel.core.exception.ServiceError;
import lombok.Data;

/**
 * The DTO for Vid Response.
 * 
 * @author Prem Kumar
 *
 */
@Data
public class VidResponseDTO {

	/** The Value to hold id */
	private String id;

	/** The Value to hold version */
	private String version;

	/** The Value to hold responseTime */
	private LocalDateTime responseTime;

	/** The Value to hold errors */
	private List<ServiceError> errors;

	/** The Value to hold response */
	private ResponseDto response;
}
