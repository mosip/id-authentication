package io.mosip.authentication.core.spi.id.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;

/**
 * The Interface IdAuthService.
 *
 * @author Arun Bose
 * @param <T> the generic type
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
	Map<String, Object> getIdByUin(String uin, boolean isBio, Set<String> filterAttributes) throws IdAuthenticationBusinessException;

	/**
	 * validates the VID.
	 *
	 * @param vid the vid
	 * @param isBio the is bio
	 * @return the id repo by vid number
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	Map<String, Object> getIdByVid(String vid, boolean isBio, Set<String> filterAttributes) throws IdAuthenticationBusinessException;

	/**
	 * Process id type.
	 *
	 * @param idvIdType the idv id type
	 * @param idvId the idv id
	 * @param isBio the is bio
	 * @param markVidConsumed the flag to mark VID consumed
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Map<String, Object> processIdType(String idvIdType, String idvId, boolean isBio, boolean markVidConsumed, Set<String> filterAttributes)
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
	 * Gets the demo data.
	 *
	 * @param identity the identity
	 * @return the demo data
	 */
	Map<String, Object> getDemoData(Map<String, Object> identity);
	
	/**
	 * Gets the bio data.
	 *
	 * @param identity the identity
	 * @return the bio data
	 */
	Map<String, Object> getBioData(Map<String, Object> identity);

	/**
	 * Gets the token .
	 *
	 * @param idResDTO the id res DTO
	 * @return the token
	 */
	String getToken(Map<String, Object> idResDTO);
	
}
