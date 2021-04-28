package io.mosip.authentication.core.spi.authtype.status.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.dto.AuthtypeStatus;

/**
 * The Interface AuthtypeStatusService - Service to check whether the
 * Auth type requested in Locked/Unlocked for authentication.
 *
 * @author Dinesh Karuppiah.T
 */
@Service
public interface AuthtypeStatusService {

	public List<AuthtypeStatus> fetchAuthtypeStatus(String token) throws IdAuthenticationBusinessException;

}
