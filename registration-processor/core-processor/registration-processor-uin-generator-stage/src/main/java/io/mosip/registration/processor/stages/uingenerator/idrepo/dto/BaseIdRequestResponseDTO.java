package io.mosip.registration.processor.stages.uingenerator.idrepo.dto;

import lombok.Data;

/**
 * The Class BaseIdRequestResponseDTO.
 *
 * @author M1049387
 */
@Data
public class BaseIdRequestResponseDTO {

	/** The id. */
	private String id;

	/** The ver. */
	private String version;

	/** The timestamp. */
	private String responsetime;
}