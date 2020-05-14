package io.mosip.authentication.common.service.integration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * This class Partner Service Manager connects to partner service to validate and 
 * get the policy file for a given partnerID, partner api key and misp license key.
 * 
 * @author Nagarjuna
 *
 */
@Component
public class PartnerServiceManager {

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(PartnerServiceManager.class);

	@Autowired
	private RestRequestFactory restRequestFactory;

	@Autowired
	private RestHelper restHelper;

	@Autowired
	protected ObjectMapper mapper;

	private static final String ERRORS = "errors";

	private static final String ERRORCODE = "errorCode";

	@SuppressWarnings("unchecked")
	public PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partner_api_key, String misp_license_key) throws IdAuthenticationBusinessException {

		RestRequestDTO buildRequest;
		PartnerPolicyResponseDTO response = null;	

		try {			
			Map<String, String> params = new HashMap<>();
			buildRequest = restRequestFactory.buildRequest(RestServicesConstants.ID_PMP_SERVICE, null, Map.class);
			params.put("partnerId", partnerId);
			params.put("partner_api_key", partner_api_key);
			params.put("misp_license_key", misp_license_key);

			buildRequest.setPathVariables(params);
			Map<String, Object> partnerServiceResponse = restHelper.requestSync(buildRequest);
			response = mapper.readValue(mapper.writeValueAsString(partnerServiceResponse.get("response")),PartnerPolicyResponseDTO.class);			
		}catch (RestServiceException e) {			
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
					e.getErrorText());
			Optional<Object> responseBody = ((RestServiceException) e).getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> partnerService = (Map<String, Object>) responseBody.get();
				if (partnerService.containsKey(ERRORS)) {
					List<Map<String, Object>> partnerServiceErrorList = (List<Map<String, Object>>) partnerService.get(ERRORS);
					if(!partnerServiceErrorList.isEmpty()) {
						if(partnerServiceErrorList.get(0).get(ERRORCODE).toString().equalsIgnoreCase(IdAuthenticationErrorConstants.PARTNER_NOT_ACTIVE.getErrorCode())) {
							throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PARTNER_DEACTIVATED.getErrorCode(),
									IdAuthenticationErrorConstants.PARTNER_DEACTIVATED.getErrorMessage(),e);
						}						
						if(partnerServiceErrorList.get(0).get(ERRORCODE).toString().equalsIgnoreCase(IdAuthenticationErrorConstants.PARTNER_NOT_MAPPED_TO_POLICY.getErrorCode())) {
							throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PARTNER_POLICY_NOTMAPPED.getErrorCode(),
									IdAuthenticationErrorConstants.PARTNER_POLICY_NOTMAPPED.getErrorMessage(),e);
						}

						if(partnerServiceErrorList.get(0).get(ERRORCODE).toString().equalsIgnoreCase(IdAuthenticationErrorConstants.MISP_LICENSE_KEY_NOT_EXISTS.getErrorCode())) {
							throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_LICENSEKEY.getErrorCode(),
									IdAuthenticationErrorConstants.INVALID_LICENSEKEY.getErrorMessage(),e);
						}

						if(partnerServiceErrorList.get(0).get(ERRORCODE).toString().equalsIgnoreCase(IdAuthenticationErrorConstants.MISP_LICENSE_KEY_EXPIRED.getErrorCode())) {
							throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.LICENSEKEY_EXPIRED.getErrorCode(),
									IdAuthenticationErrorConstants.LICENSEKEY_EXPIRED.getErrorMessage(),e);
						}

						if(partnerServiceErrorList.get(0).get(ERRORCODE).toString().equalsIgnoreCase(IdAuthenticationErrorConstants.PARTNER_NOT_REGISTRED.getErrorCode())) {
							throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorCode(),
									IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorMessage(),e);
						}
					}else {
						throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
								IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), e);
					}
				}else {
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
							IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), e);
				}
			}else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), e);
			}			
		} catch (JsonParseException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} catch (JsonMappingException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} catch (JsonProcessingException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		} catch (IOException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
		return response;
	}
}
