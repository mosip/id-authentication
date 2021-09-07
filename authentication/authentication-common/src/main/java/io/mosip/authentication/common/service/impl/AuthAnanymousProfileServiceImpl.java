package io.mosip.authentication.common.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.impl.idevent.AnanymousAuthenticationProfile;
import io.mosip.authentication.common.service.websub.impl.AuthAnanymouseEventPublisher;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.profile.AuthAnanymousProfileService;

@Service
public class AuthAnanymousProfileServiceImpl implements AuthAnanymousProfileService {
	
	@Autowired
	private IdInfoFetcher idInfoFetcher;
	
	@Autowired
	private AuthAnanymouseEventPublisher authAnanymouseEventPublisher;

	@Override
	public void storeAnanymousProfile(AuthRequestDTO authRequestDto, AuthResponseDTO response,
			Map<String, List<IdentityInfoDTO>> idInfo) {
		AnanymousAuthenticationProfile ananymousProfile = createAnanymousProfile();
		storeAnanymousProfile(ananymousProfile);
		authAnanymouseEventPublisher.publishEvent(ananymousProfile);
	}

	private void storeAnanymousProfile(AnanymousAuthenticationProfile ananymousProfile) {
		// TODO Auto-generated method stub
		
	}

	private AnanymousAuthenticationProfile createAnanymousProfile() {
		// TODO Auto-generated method stub
		return null;
	}

}
