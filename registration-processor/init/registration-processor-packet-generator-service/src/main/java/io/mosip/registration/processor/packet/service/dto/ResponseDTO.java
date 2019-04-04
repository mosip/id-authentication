package io.mosip.registration.processor.packet.service.dto;

import java.util.List;

/**
 * The Class ResponseDTO.
 */
public class ResponseDTO {

	/** The error response DT os. */
	private List<ErrorResponseDTO> errorResponseDTOs;

	/** The success response DTO. */
	private SuccessResponseDTO successResponseDTO;

	/**
	 * Gets the error response DT os.
	 *
	 * @return the error response DT os
	 */
	public List<ErrorResponseDTO> getErrorResponseDTOs() {
		return errorResponseDTOs;
	}

	/**
	 * Sets the error response DT os.
	 *
	 * @param errorResponseDTOs
	 *            the new error response DT os
	 */
	public void setErrorResponseDTOs(List<ErrorResponseDTO> errorResponseDTOs) {
		this.errorResponseDTOs = errorResponseDTOs;
	}

	/**
	 * Gets the success response DTO.
	 *
	 * @return the success response DTO
	 */
	public SuccessResponseDTO getSuccessResponseDTO() {
		return successResponseDTO;
	}

	/**
	 * Sets the success response DTO.
	 *
	 * @param successResponseDTO
	 *            the new success response DTO
	 */
	public void setSuccessResponseDTO(SuccessResponseDTO successResponseDTO) {
		this.successResponseDTO = successResponseDTO;
	}

}
