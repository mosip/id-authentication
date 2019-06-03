package io.mosip.idrepository.core.dto;

import java.util.List;

import lombok.Data;

/**
 * The Class ResponseDTO.
 *
 * @author Manoj SP
 */
@Data
public class BaseRequestResponseDTO {

	/** The status. */
	private String status;

	/** The identity. */
	private Object identity;

	private List<DocumentDTO> documents;
}
