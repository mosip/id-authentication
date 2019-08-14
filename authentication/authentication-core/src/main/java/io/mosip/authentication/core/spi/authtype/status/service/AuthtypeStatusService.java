package io.mosip.authentication.core.spi.authtype.status.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.authtype.dto.AuthtypeRequestDto;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */
@Service
public interface AuthtypeStatusService {

	public List<AuthtypeStatus> fetchAuthtypeStatus(AuthtypeRequestDto authtypeRequestDto)
			throws IdAuthenticationBusinessException;
	
	public List<AuthtypeStatus> fetchAuthtypeStatus(String individualId, String individualIdType) throws IdAuthenticationBusinessException;

}
