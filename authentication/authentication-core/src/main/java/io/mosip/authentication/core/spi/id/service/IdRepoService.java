package io.mosip.authentication.core.spi.id.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.dto.idrepo.IdResponseDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public interface IdRepoService {
	public Map<String, Object> getIdRepo(String uin) throws IdAuthenticationBusinessException;
	Map<String, List<IdentityInfoDTO>> getIdInfo(Map<String, Object> idResponseDTO) throws IdAuthenticationBusinessException;
}
