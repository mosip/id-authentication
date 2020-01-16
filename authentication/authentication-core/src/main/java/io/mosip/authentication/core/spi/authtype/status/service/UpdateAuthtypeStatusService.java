
package io.mosip.authentication.core.spi.authtype.status.service;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.authtype.dto.UpdateAuthtypeStatusResponseDto;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

@Service
public interface UpdateAuthtypeStatusService {

	public UpdateAuthtypeStatusResponseDto updateAuthtypeStatus(AuthTypeStatusDto updateAuthtypeStatusRequestDto)
			throws IdAuthenticationBusinessException;
}
