package io.mosip.authentication.core.spi.id.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;

/**
 * The Interface IdAuthService.
 *
 * @author Arun Bose
 */

public interface IdService<T> {

	/**
	 * validates the UIN.
	 *
	 * @param uin the uin
	 * @param isBio the is bio
	 * @return the id repo by uin number
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	Map<String, Object> getIdByUin(String uin, boolean isBio) throws IdAuthenticationBusinessException;

	/**
	 * validates the VID.
	 *
	 * @param vid the vid
	 * @param isBio the is bio
	 * @return the id repo by vid number
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	Map<String, Object> getIdByVid(String vid, boolean isBio) throws IdAuthenticationBusinessException;

	/**
	 * Process the IdType and validates the Idtype and upon validation reference Id
	 * is returned in AuthRequestDTO.
	 *
	 * @param idvIdType idType
	 * @param idvId     id-number
	 * @param isBio the is bio
	 * @return map
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public Map<String, Object> processIdType(String idvIdType, String idvId, boolean isBio)
			throws IdAuthenticationBusinessException;

	/**
	 * Store entry in Auth_txn table for all authentications.
	 *
	 * @param t the t
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public void saveAutnTxn(T t) throws IdAuthenticationBusinessException;
	
	
	/**
	 * Method to get Identity info.
	 *
	 * @param idResponseDTO the id response DTO
	 * @return the id info
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	Map<String, List<IdentityInfoDTO>> getIdInfo(Map<String, Object> idResponseDTO)
			throws IdAuthenticationBusinessException;
}
