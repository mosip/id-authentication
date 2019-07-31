package io.mosip.authentication.internal.service.impl;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.authtype.dto.UpdateAuthtypeStatusRequestDto;
import io.mosip.authentication.core.authtype.dto.UpdateAuthtypeStatusResponseDto;
import io.mosip.authentication.core.spi.authtype.status.service.UpdateAuthtypeStatusService;

@Service
public class UpdateAuthtypeStatusServiceImpl implements UpdateAuthtypeStatusService{

	public UpdateAuthtypeStatusResponseDto UpdateAuthtypeStatus(
			UpdateAuthtypeStatusRequestDto updateAuthtypeStatusRequestDto) {
		return null;
	}

}
