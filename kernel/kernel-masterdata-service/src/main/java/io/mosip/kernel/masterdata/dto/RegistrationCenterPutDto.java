package io.mosip.kernel.masterdata.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RegistrationCenterPutDto {

	@NotBlank
	private List<RegistrationCenterPutReqAdmDto> registrationCenterPutReqAdmDtos;

}
