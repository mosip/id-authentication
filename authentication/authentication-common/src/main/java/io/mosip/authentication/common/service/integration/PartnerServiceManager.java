package io.mosip.authentication.common.service.integration;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.cache.PartnerServiceCache;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * This class Partner Service Manager connects to partner service to validate
 * and get the policy file for a given partnerID, partner api key and misp
 * license key.
 * 
 * @author Nagarjuna
 *
 */
@Component
public class PartnerServiceManager {

	private static final String PARNER_ACTIVE_STATUS = "Active";

	/** The logger. */
	private static final Logger logger = IdaLogger.getLogger(PartnerServiceManager.class);

	@Autowired
	private RestRequestFactory restRequestFactory;

	@Autowired
	@Qualifier("external")
	private RestHelper restHelper;

	@Autowired
	protected ObjectMapper mapper;

	private static final String ERRORS = "errors";

	private static final String ERRORCODE = "errorCode";

	@Autowired
	private PartnerServiceCache partnerServiceCache;

	@Autowired
	private CacheManager cacheManager;

	@SuppressWarnings("unchecked")
	public PartnerPolicyResponseDTO validateAndGetPolicy(String partnerId, String partner_api_key,
			String misp_license_key) throws IdAuthenticationBusinessException {

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
			response = mapper.readValue(mapper.writeValueAsString(partnerServiceResponse.get("response")),
					PartnerPolicyResponseDTO.class);
		} catch (RestServiceException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(),
					e.getErrorText());
			Optional<Object> responseBody = ((RestServiceException) e).getResponseBody();
			if (responseBody.isPresent()) {
				Map<String, Object> partnerService = (Map<String, Object>) responseBody.get();
				if (partnerService.containsKey(ERRORS)) {
					List<Map<String, Object>> partnerServiceErrorList = (List<Map<String, Object>>) partnerService
							.get(ERRORS);
					if (!partnerServiceErrorList.isEmpty()) {
						throw getMatchingErrorCodes(partnerServiceErrorList.get(0).get(ERRORCODE).toString(), e);
					}
				} else {
					throw new IdAuthenticationBusinessException(
							IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
							IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), e);
				}
			} else {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
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

	public void evictPartnerBasedOnPartnerId(String partnerId) {
		this.evictPartner(partnerId, null, null);
	}
	
	public void evictPartnerBasedOnPartnerApiKey(String partnerApiKey) {
		this.evictPartner(null, partnerApiKey, null);
	}
	
	public void evictPartnerBasedOnMispLicenseKey(String mispLicenseKey) {
		this.evictPartner(null, null, mispLicenseKey);
	}

	@SuppressWarnings("unchecked")
	public void evictPartnerBasedOnPolicyId(String policyId) {
		Map<PartnerDTO, PartnerPolicyResponseDTO> partnerCacheMap = (Map<PartnerDTO, PartnerPolicyResponseDTO>) cacheManager
				.getCache("partner").getNativeCache();
		partnerCacheMap.entrySet().stream()
				.filter(partnerEntry -> partnerEntry.getValue().getPolicyId().contentEquals(policyId))
				.forEach(partnerEntry -> partnerServiceCache.evictPartnerPolicy(partnerEntry.getKey()));
	}
	
	@SuppressWarnings("unchecked")
	private void evictPartner(String partnerId, String partnerApiKey, String mispLicenseKey) {
		Map<PartnerDTO, PartnerPolicyResponseDTO> partnerCacheMap = (Map<PartnerDTO, PartnerPolicyResponseDTO>) cacheManager
				.getCache("partner").getNativeCache();
		partnerCacheMap.keySet().stream().filter(
				partnerKey -> partnerKey.getPartnerId().contentEquals(Objects.nonNull(partnerId) ? partnerId : "")
						|| partnerKey.getPartnerApiKey().contentEquals(Objects.nonNull(partnerApiKey) ? partnerApiKey : "")
						|| partnerKey.getMispLicenseKey()
								.contentEquals(Objects.nonNull(mispLicenseKey) ? mispLicenseKey : ""))
				.forEach(partnerServiceCache::evictPartnerPolicy);
	}

	/**
	 * This method will throw ida exceptions corresponding to pmp
	 * 
	 * @param erroCode
	 * @param e
	 * @return
	 */
	private IdAuthenticationBusinessException getMatchingErrorCodes(String erroCode, RestServiceException e) {
		switch (erroCode) {
		case "PMS_PMP_020":
			return new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.INVALID_LICENSEKEY.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_LICENSEKEY.getErrorMessage(), e);
		case "PMS_PMP_021":
			return new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.LICENSEKEY_EXPIRED.getErrorCode(),
					IdAuthenticationErrorConstants.LICENSEKEY_EXPIRED.getErrorMessage(), e);
		case "PMS_PMP_025":
			return new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.LICENSEKEY_SUSPENDED.getErrorCode(),
					IdAuthenticationErrorConstants.LICENSEKEY_SUSPENDED.getErrorMessage(), e);
		case "PMS_PMP_016":
			return new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.PARTNER_DEACTIVATED.getErrorCode(),
					IdAuthenticationErrorConstants.PARTNER_DEACTIVATED.getErrorMessage(), e);
		case "PMS_PMP_013":
			return new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorCode(),
					IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorMessage(), e);
		case "PMS_PMP_017":
			return new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.PARTNER_POLICY_NOTMAPPED.getErrorCode(),
					IdAuthenticationErrorConstants.PARTNER_POLICY_NOTMAPPED.getErrorMessage(), e);
		case "PMS_PMP_023":
			return new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.INVALID_POLICY_ID.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_POLICY_ID.getErrorMessage(), e);
		case "PMS_PMP_019":
			return new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_ACTIVE.getErrorCode(),
					IdAuthenticationErrorConstants.PARTNER_POLICY_NOT_ACTIVE.getErrorMessage(), e);
		case "PMS_PMP_018":
			return new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.INVALID_POLICY_ID.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_POLICY_ID.getErrorMessage(), e);
		case "PMS_PMP_024":
			return new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorCode(),
					IdAuthenticationErrorConstants.PARTNER_NOT_REGISTERED.getErrorMessage(), e);

		default:
			return new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getPartnerIds() {
		try {
			Map<String, Object> responseWrapperMap = restHelper.requestSync(
					restRequestFactory.buildRequest(RestServicesConstants.PARTNER_SERVICE, null, Map.class));
			Object response = responseWrapperMap.get("response");
			if (response instanceof Map) {
				Map<String, Object> responseMap = (Map<String, Object>) response;
				Object partners = responseMap.get("partners");
				if (partners instanceof List) {
					List<Map<String, Object>> partnersList = (List<Map<String, Object>>) partners;
					List<String> partnerIds = partnersList.stream()
							.filter(partner -> PARNER_ACTIVE_STATUS.equalsIgnoreCase((String) partner.get("status")))
							.map(partner -> (String) partner.get("partnerID")).collect(Collectors.toList());
					return partnerIds;
				}
			}
		} catch (RestServiceException | IDDataValidationException e) {
			logger.error(IdRepoSecurityManager.getUser(), this.getClass().getSimpleName(), "getPartnerIds",
					e.getMessage());
		}
		return Collections.emptyList();
	}
}
