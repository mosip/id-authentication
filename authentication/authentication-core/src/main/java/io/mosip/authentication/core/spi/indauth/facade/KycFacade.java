package io.mosip.authentication.core.spi.indauth.facade;

import java.util.Map;
import javax.annotation.Nonnull;

import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.EkycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.EKycAuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthResponseDTO;

/**
 * This class used to integrate with kyc service
 * 
 * @author Sanjay Murali
 */
public interface KycFacade {
	
	/**
	 * Process the EKycAuthRequestDTO to integrate with EKycService.
	 *
	 * @param eKycAuthRequestDTO is DTO of KycAuthRequestDTO
	 * @param authResponseDTO   the auth response DTO
	 * @param partnerId the partner id
	 * @param metadata the metadata
	 * @return the kyc auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	EKycAuthResponseDTO processEKycAuth(@Nonnull EkycAuthRequestDTO eKycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId, Map<String, Object>  metadata) throws IdAuthenticationBusinessException;
	
	/**
	 * Authenticate individual.
	 *
	 * @param authRequest the auth request
	 * @param request the request
	 * @param partnerId the partner id
	 * @return the auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 * @throws IdAuthenticationDaoException the id authentication dao exception
	 */
	AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequest, boolean request, String partnerId, String partnerApiKey, ObjectWithMetadata requestWithMetadata)
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException;

	
	
	/**
	 * Process the KycAuthRequestDTO to integrate with KYCService.
	 *
	 * @param kycAuthRequestDTO is DTO of KycAuthRequestDTO
	 * @param authResponseDTO   the auth response DTO
	 * @param partnerId the partner id
	 * @param metadata the metadata
	 * @return the kyc auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	KycAuthResponseDTO processKycAuth(@Nonnull AuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO,
			String partnerId, String oidcClientId, Map<String, Object>  metadata) throws IdAuthenticationBusinessException;

}
