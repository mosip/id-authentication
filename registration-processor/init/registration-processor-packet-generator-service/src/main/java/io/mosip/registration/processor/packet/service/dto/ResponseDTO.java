package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * The Class ResponseDTO.
 */
@Data
public class ResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6164646139118699385L;

	/** The error response DT os. */
	private List<ErrorResponseDTO> errorResponseDTOs;

	/** The success response DTO. */
	private SuccessResponseDTO successResponseDTO;

}
