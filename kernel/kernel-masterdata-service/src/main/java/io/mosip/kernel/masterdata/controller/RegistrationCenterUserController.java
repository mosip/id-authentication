package io.mosip.kernel.masterdata.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.UserAndRegCenterMappingResponseDto;
import io.mosip.kernel.masterdata.service.RegistrationCenterUserService;
import io.swagger.annotations.Api;

/**
 * 
 * @author Megha Tanga
 *
 */
@RestController
@RequestMapping("/registrationcenteruser")
@Api(value = "Operation related to mapping of registration center and users", tags = { "RegistrationCenterUser" })
public class RegistrationCenterUserController {

	@Autowired
	RegistrationCenterUserService registrationCenterUserService;

	/**
	 * Api to un-map User from a Registration Center .
	 * 
	 * @param userId
	 *            the user Id
	 * @param regCenterId
	 *            the Registration Center ID.
	 * @return the UserAndRegCenterMappingResponseDto.
	 */
	@PreAuthorize("hasRole('ZONAL_ADMIN')")
	@ResponseFilter
	@PutMapping("/unmap/{userid}/{regcenterid}")
	public ResponseWrapper<UserAndRegCenterMappingResponseDto> unmapUserRegCenter(
			@PathVariable("userid") @NotBlank @Size(min = 1, max = 36) String userId,
			@PathVariable("regcenterid") @NotBlank @Size(min = 1, max = 10) String regCenterId) {

		ResponseWrapper<UserAndRegCenterMappingResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterUserService.unmapUserRegCenter(userId, regCenterId));
		return responseWrapper;
	}

	/**
	 * Api to map User to a Registration Center.
	 * 
	 * @param userId
	 *            the user Id
	 * @param regCenterId
	 *            the registration center id
	 * @return the UserAndRegCenterMappingResponseDto.
	 */
	@PreAuthorize("hasRole('ZONAL_ADMIN')")
	@ResponseFilter
	@PutMapping("/map/{userid}/{regcenterid}")
	public ResponseWrapper<UserAndRegCenterMappingResponseDto> mapUserRegCenter(
			@PathVariable("userid") @NotBlank @Size(min = 1, max = 36) String userId,
			@PathVariable("regcenterid") @NotBlank @Size(min = 1, max = 10) String regCenterId) {

		ResponseWrapper<UserAndRegCenterMappingResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(registrationCenterUserService.mapUserRegCenter(userId, regCenterId));
		return responseWrapper;
	}

}
