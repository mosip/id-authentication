package io.kernel.core.idrepo.service;

import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.kernel.core.idrepo.dto.IdResponseDTO;
import io.kernel.core.idrepo.entity.Uin;
import io.kernel.core.idrepo.exception.IdRepoAppException;

/**
 * @author Manoj SP
 *
 */
public interface IdRepoService {
	
	IdResponseDTO addIdentity(IdRequestDTO request) throws IdRepoAppException;
	
	IdResponseDTO retrieveIdentity(String uin) throws IdRepoAppException;
	
	IdResponseDTO updateIdentity(IdRequestDTO request) throws IdRepoAppException;
	
	String generateUIN() throws IdRepoAppException;
	
	Boolean checkUIN(String uin) throws IdRepoAppException;
	
	IdResponseDTO constructIdResponse(String id, Uin uin) throws IdRepoAppException;
}
