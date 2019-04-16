package io.mosip.authentication.core.spi.id.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The Interface IdAuthService.
 *
 * @author Arun Bose
 */

public interface IdAuthService<T> {

	/**
	 * validates the UIN.
	 *
	 * @param uin the uin
	 * @return the id repo by uin number
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	Map<String, Object> getIdByUin(String uin, boolean isBio) throws IdAuthenticationBusinessException;

	/**
	 * validates the VID.
	 *
	 * @param vid the vid
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
	 * @return map
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public Map<String, Object> processIdType(String idvIdType, String idvId, boolean isBio)
			throws IdAuthenticationBusinessException;

	/**
	 * Store entry in Auth_txn table for all authentications.
	 *
	 * @param idvId       idvId
	 * @param idvIdType   idvIdType(D/V)
	 * @param reqTime     reqTime
	 * @param txnId       txnId
	 * @param status      status('Y'/'N')
	 * @param comment     comment
	 * @param requestType requestType(OTP_REQUEST,OTP_AUTH,DEMO_AUTH,BIO_AUTH)
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
