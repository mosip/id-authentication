package io.mosip.resident.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationStatusResponseDTO extends BaseResponseDTO implements Serializable {
	private static final long serialVersionUID = 2720322794361261840L;
	private RegistrationStatusDTO response;
	private List<ErrorDTO> errors;
}
