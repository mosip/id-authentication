package io.mosip.idrepository.vid.dto;
/**
 * The DTO for Vid Request
 * @author Prem Kumar.
 *
 */

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VidRequestDTO {

	/** The Value to hold id */
	private String id;

	/** The Value to hold version */
	private String version;

	/** The Value to hold requestTime */
	private LocalDateTime requestTime;

	/** The Value to hold request */
	private RequestDto request;
}
