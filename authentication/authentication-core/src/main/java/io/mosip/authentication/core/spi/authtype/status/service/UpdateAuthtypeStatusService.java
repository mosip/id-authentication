
package io.mosip.authentication.core.spi.authtype.status.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.dto.AuthtypeStatus;

@Service
public interface UpdateAuthtypeStatusService {

	public void updateAuthTypeStatus(String tokenId, List<AuthtypeStatus> authTypeStatusList)
			throws IdAuthenticationBusinessException;
}
