package io.mosip.authentication.core.spi.id.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * @author Dinesh Karuppiah.T
 */
public interface IdRepoService {

	/**
	 * Method to get entity from ID Repo
	 * 
	 * @param uin
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	public Map<String, Object> getIdRepo(String uin) throws IdAuthenticationBusinessException;

	/**
	 * Method to get Identity info
	 * 
	 * @param idResponseDTO
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	Map<String, List<IdentityInfoDTO>> getIdInfo(Map<String, Object> idResponseDTO)
			throws IdAuthenticationBusinessException;
}
