package io.mosip.authentication.kyc.service.filter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.filter.IdAuthFilter;
import io.mosip.authentication.common.service.policy.dto.AuthPolicy;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class KycAuthFilter - used to authenticate the request and manipulate
 * response received for KYC request
 * 
 * @author Sanjay Murali
 */
@Component
public class KycAuthFilter extends IdAuthFilter {

	/** The Constant KYC. */
	private static final String KYC = "kyc";

	/** The Constant IDENTITY. */
	private static final String IDENTITY = "identity";

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#encodedResponse(java.
	 * util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, Object> encipherResponse(Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		try {
			Map<String, Object> response = (Map<String, Object>) responseBody.get(IdAuthCommonConstants.RESPONSE);
			response.put(IDENTITY, keyManager.encryptData(response, mapper));
			responseBody.put(IdAuthCommonConstants.RESPONSE, response);
			return responseBody;
		} catch (IdAuthenticationAppException | ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_ENCRYPT_EKYC_RESPONSE, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#setTxnId(java.util.Map,
	 * java.util.Map)
	 */
	@Override
	protected Map<String, Object> setResponseParams(Map<String, Object> requestBody, Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		Map<String, Object> responseParams = super.setResponseParams(requestBody, responseBody);
		setKycParams(responseParams);
		Object response = responseParams.get(IdAuthCommonConstants.RESPONSE);
		responseParams.put(IdAuthCommonConstants.RESPONSE, response);
		return responseParams;
	}

	/**
	 * setKycParams method used to constructs the KYC response and removes null and
	 * empty value
	 *
	 * @param response the response
	 * @return the map
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> setKycParams(Map<String, Object> response) {
		Object kyc = response.get(IdAuthCommonConstants.RESPONSE);
		Map<String, Object> kycDetails = null;
		if (kyc instanceof Map) {
			kycDetails = (Map<String, Object>) kyc;
			Object identity = kycDetails.get(IDENTITY);
			if (identity instanceof Map) {
				Map<String, Object> kycIdentityMap = constructKycInfo((Map<String, Object>) identity);
				kycDetails.put(IDENTITY, kycIdentityMap);

			}
		}
		return kycDetails;
	}

	/**
	 * constructKycInfo method used to manipulate the identity information check
	 * null or empty value
	 *
	 * @param identity the identity
	 * @return the map
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> constructKycInfo(Map<String, Object> identity) {
		Map<String, Object> responseMap = new HashMap<>();
		identity.entrySet().stream().forEach(entry -> {
			if(entry.getValue() instanceof List) {
				List<Map<String, Object>> listOfMap = (List<Map<String, Object>>) entry.getValue();
				Object value = Objects.isNull(listOfMap) ? listOfMap
						: listOfMap.stream()
						.map((Map<String, Object> map) -> map.entrySet().stream()
								.filter(innerEntry -> innerEntry.getValue() != null
								|| !innerEntry.getKey().equals("language"))
								.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (map1, map2) -> map1,
										LinkedHashMap::new)))
						.collect(Collectors.toList());
				responseMap.put(entry.getKey(), value);
			} else {
				responseMap.put(entry.getKey(), entry.getValue());
			}
		});
		return responseMap;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.IdAuthFilter#validateSignature(java.
	 * lang.String, byte[])
	 */
	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.filter.IdAuthFilter#
	 * checkAllowedAuthTypeBasedOnPolicy(java.util.Map, java.util.List)
	 */
	@Override
	protected void checkAllowedAuthTypeBasedOnPolicy(Map<String, Object> requestBody, List<AuthPolicy> authPolicies)
			throws IdAuthenticationAppException {
		if (!isAllowedAuthType(KYC, authPolicies)) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNAUTHORISED_PARTNER.getErrorCode(),
					IdAuthenticationErrorConstants.UNAUTHORISED_PARTNER.getErrorMessage());

		}
		super.checkAllowedAuthTypeBasedOnPolicy(requestBody, authPolicies);
	}

}
