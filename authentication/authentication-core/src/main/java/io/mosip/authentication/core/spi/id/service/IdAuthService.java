package io.mosip.authentication.core.spi.id.service;

import java.util.Map;

import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * The Interface IdAuthService.
 *
 * @author Arun Bose
 */

public interface IdAuthService {

	/**
	 * validates the UIN.
	 *
	 * @param uin the uin
	 * @return the id repo by uin number
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	Map<String, Object> getIdRepoByUinNumber(String uin) throws IdAuthenticationBusinessException;

	/**
	 * validates the VID.
	 *
	 * @param vid the vid
	 * @return the id repo by vid number
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	Map<String, Object> getIdRepoByVidNumber(String vid) throws IdAuthenticationBusinessException;

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
	public Map<String, Object> processIdType(String idvIdType, String idvId) throws IdAuthenticationBusinessException;

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
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public void saveAutnTxn(String idvId, String idvIdType, String reqTime, String txnId, String status, String comment,
			RequestType requestType) throws IdAuthenticationBusinessException;
}
