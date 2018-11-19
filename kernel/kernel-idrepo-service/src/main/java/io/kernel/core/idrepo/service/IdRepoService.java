package io.kernel.core.idrepo.service;

import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.kernel.core.idrepo.dto.IdResponseDTO;
import io.kernel.core.idrepo.entity.Uin;

/**
 * @author Manoj SP
 *
 */
public interface IdRepoService {
	
	IdResponseDTO addIdentity(IdRequestDTO request);
	
	IdResponseDTO retrieveIdentity(String uin);
	
	IdResponseDTO updateIdentity(IdRequestDTO request);
	
	Boolean checkUIN(String uin);
	
	IdResponseDTO constructIdResponse(String id, Uin uin);
}
