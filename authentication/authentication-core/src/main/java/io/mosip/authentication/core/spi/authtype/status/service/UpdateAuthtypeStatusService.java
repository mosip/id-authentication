
package io.mosip.authentication.core.spi.authtype.status.service;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

@Service
public interface UpdateAuthtypeStatusService {

	public void updateAuthtypeStatus(AuthTypeStatusDto updateAuthtypeStatusRequestDto)
			throws IdAuthenticationBusinessException;
}
