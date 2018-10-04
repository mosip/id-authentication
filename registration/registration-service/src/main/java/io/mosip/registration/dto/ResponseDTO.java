package io.mosip.registration.dto;

import java.util.List;

import lombok.Data;

@Data
public class ResponseDTO {
	private List<ErrorResponseDTO> errorResponseDTOs;
	private SuccessResponseDTO successResponseDTO;
}
