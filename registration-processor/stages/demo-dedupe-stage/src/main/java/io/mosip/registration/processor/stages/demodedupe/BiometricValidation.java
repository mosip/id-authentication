package io.mosip.registration.processor.stages.demodedupe;

import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;

//remove the class when auth is fixed
@Component
public class BiometricValidation {
	public boolean validateBiometric(String duplicateUin) throws ApisResourceAccessException {
		/*
		 * authRequestDTO.setIdvId(duplicateUin);
		 * authRequestDTO.setAuthType(authTypeDTO); request.setIdentity(identityDTO);
		 * authRequestDTO.setRequest(request);
		 * 
		 * AuthResponseDTO authResponseDTO = (AuthResponseDTO)
		 * restClientService.postApi(ApiName.AUTHINTERNAL, "", "", authRequestDTO,
		 * AuthResponseDTO.class); return authResponseDTO != null &&
		 * authResponseDTO.getStatus() != null &&
		 * authResponseDTO.getStatus().equalsIgnoreCase("y");
		 */
		return true;
	}
}
