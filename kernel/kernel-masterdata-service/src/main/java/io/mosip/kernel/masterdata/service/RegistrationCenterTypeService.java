package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterTypeRequestDto;

public interface RegistrationCenterTypeService {
	PostResponseDto addRegistrationCenterType(RegistrationCenterTypeRequestDto registrationCenterTypeRequestDto);

}
