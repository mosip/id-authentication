package io.mosip.authentication.core.spi.indauth.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.VCResponseDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeRequestDTO;

/**
 * This interface is used to build Verifiable Credentials.
 * 
 * @author Mahammed Taheer
 */
public interface VciService {

	/**
	 * Method used to add the Credential Subject Id in DB.
	 *
	 * @param credSubjectId      the Credential Subject id of the identity
	 * @param idVidHash       	 the Id/VID hash value
	 * @param tokenId            the token id of the identity
	 * @return void
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public void addCredSubjectId(String credSubjectId,	
				String idVidHash, String tokenId, String oidcClientId) throws IdAuthenticationBusinessException;

	
	/**
	 * Method to build the verifiable credentials.
	 *
	 * @param credSubjectId      the Credential Subject id of the identity
	 * @param vcFormat			 VC format 
	 * @param idInfo			 List of Identity Info of the user.
	 * @param locales			 locale data to be added in VC.
	 * @return VCResponseDTO	 VC Response based on requested format.
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public VCResponseDTO<?> buildVerifiableCredentials(String credSubjectId, String vcFormat, 	
				Map<String, List<IdentityInfoDTO>> idInfo, List<String> locales, Set<String> allowedAttributes,
				VciExchangeRequestDTO vciExchangeRequestDTO, String psuToken) throws IdAuthenticationBusinessException;
}
