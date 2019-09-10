package io.mosip.authentication.core.spi.authtype.status.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.authtype.dto.AuthtypeRequestDto;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The Interface AuthtypeStatusService - Service to check whether the
 * Auth type requested in Locked/Unlocked for authentication.
 *
 * @author Dinesh Karuppiah.T
 */
@Service
public interface AuthtypeStatusService {

	/**
	 * Fetch authtype status.
	 *
	 * @param authtypeRequestDto the authtype request dto
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public List<AuthtypeStatus> fetchAuthtypeStatus(AuthtypeRequestDto authtypeRequestDto)
			throws IdAuthenticationBusinessException;
	
	/**
	 * Fetch authtype status.
	 *
	 * @param individualId the individual id
	 * @param individualIdType the individual id type
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public List<AuthtypeStatus> fetchAuthtypeStatus(String individualId, String individualIdType) throws IdAuthenticationBusinessException;

}
