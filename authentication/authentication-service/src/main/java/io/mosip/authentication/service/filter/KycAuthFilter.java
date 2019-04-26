package io.mosip.authentication.service.filter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.service.policy.AuthPolicy;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * The Class KycAuthFilter - used to authenticate the request 
 * and manipulate response received for KYC request
 * 
 * @author Sanjay Murali
 */
@Component
public class KycAuthFilter extends IdAuthFilter {

	/** The Constant KYC. */
	private static final String KYC = "kyc";

	/** The Constant IDENTITY. */
	private static final String IDENTITY = "identity";

	/** The Constant RESPONSE. */
	private static final String RESPONSE = "response";

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
			Map<String, Object> response = (Map<String, Object>) responseBody.get(RESPONSE);
			Map<String, Object> identity = response.get(IDENTITY) instanceof Map ? (Map<String, Object>) response.get(IDENTITY) : null;
			if (Objects.nonNull(identity)) {
				if (Objects.nonNull(publicKey)) {
					response.put(IDENTITY, encryptKycResponse(identity));
				} else {
					response.put(IDENTITY, encode(toJsonString(identity)));
				}
			}
			responseBody.put(RESPONSE, response);
			return responseBody;
		} catch (ClassCastException | JsonProcessingException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * encryptKycResponse method is used to encode and encipher the
	 * response.
	 *
	 * @param identity the response
	 * @return the string
	 * @throws JsonProcessingException the json processing exception
	 */
	private String encryptKycResponse(Map<String, Object> identity) throws JsonProcessingException {
		byte[] symmetricDataEncrypt = null;
		byte[] asymmetricKeyEncrypt = null;
		if (Objects.nonNull(identity)) {
			SecretKey symmetricKey = keyManager.getSymmetricKey();
			symmetricDataEncrypt = encryptor.symmetricEncrypt(symmetricKey, toJsonString(identity).getBytes());
			asymmetricKeyEncrypt = encryptor.asymmetricPublicEncrypt(publicKey, symmetricKey.getEncoded());
		}

		if (Objects.nonNull(asymmetricKeyEncrypt) && Objects.nonNull(symmetricDataEncrypt)) {
			return CryptoUtil.encodeBase64String(asymmetricKeyEncrypt)
					.concat(CryptoUtil.encodeBase64String(symmetricDataEncrypt));
		}
		return null;
	}

	/**
	 * toJsonString method converts a object to a JSON string
	 *
	 * @param map the map
	 * @return the string
	 * @throws JsonProcessingException the json processing exception
	 */
	private String toJsonString(Object map) throws JsonProcessingException {
		return mapper.writerFor(Map.class).writeValueAsString(map);
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
		Object response = responseParams.get(RESPONSE);
		responseParams.put(RESPONSE, response);
		return responseParams;
	}

	/**
	 * setKycParams method used to constructs the KYC response
	 * and removes null and empty value
	 *
	 * @param response the response
	 * @return the map
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> setKycParams(Map<String, Object> response) {
		Object kyc = response.get(RESPONSE);
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
	 * constructKycInfo method used to manipulate the
	 * identity information check null or empty value
	 *
	 * @param identity the identity
	 * @return the map
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> constructKycInfo(Map<String, Object> identity) {
		Map<String, Object> responseMap = new HashMap<>();
		identity.entrySet().stream().forEach(entry -> {
			List<Map<String, Object>> listOfMap = (List<Map<String, Object>>) entry.getValue();
			Object value = Objects.isNull(listOfMap) ? listOfMap : listOfMap.stream().map((Map<String, Object> map) -> map.entrySet().stream()
					.filter(innerEntry -> innerEntry.getValue() != null || !innerEntry.getKey().equals("language"))
					.collect(
							Collectors.toMap(Entry::getKey, Entry::getValue, (map1, map2) -> map1, LinkedHashMap::new)))
					.collect(Collectors.toList());
			responseMap.put(entry.getKey(), value);
		});
		return responseMap;

	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.IdAuthFilter#validateSignature(java.lang.String, byte[])
	 */
	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.IdAuthFilter#checkAllowedAuthTypeBasedOnPolicy(java.util.Map, java.util.List)
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
