package io.mosip.authentication.core.spi.authtype.status.service;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.authtype.dto.UpdateAuthtypeStatusRequestDto;
import io.mosip.authentication.core.authtype.dto.UpdateAuthtypeStatusResponseDto;

@Component
public interface UpdateAuthtypeStatusService {

	public UpdateAuthtypeStatusResponseDto UpdateAuthtypeStatus(UpdateAuthtypeStatusRequestDto updateAuthtypeStatusRequestDto);
}
