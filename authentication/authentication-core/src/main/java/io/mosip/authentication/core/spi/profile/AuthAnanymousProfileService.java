package io.mosip.authentication.core.spi.profile;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;

public interface AuthAnanymousProfileService {
	
	void storeAnanymousProfile(AuthRequestDTO authRequestDto, AuthResponseDTO authResponseDTO, Map<String, List<IdentityInfoDTO>> idInfo);

}
