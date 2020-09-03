
package io.mosip.authentication.core.spi.authtype.status.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.dto.AuthtypeStatus;

/**
 * The Interface UpdateAuthtypeStatusService.
 */
@Service
public interface UpdateAuthtypeStatusService {

	/**
	 * Update auth type status.
	 *
	 * @param tokenId the token id
	 * @param authTypeStatusList the auth type status list
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public void updateAuthTypeStatus(String tokenId, List<AuthtypeStatus> authTypeStatusList)
			throws IdAuthenticationBusinessException;
}
