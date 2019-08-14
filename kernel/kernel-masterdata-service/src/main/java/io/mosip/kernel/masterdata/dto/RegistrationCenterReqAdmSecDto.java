package io.mosip.kernel.masterdata.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RegistrationCenterReqAdmSecDto {
	

	@NotBlank
	List<RegCenterPostReqDto> registrationCenterDtos;
	
	


}
