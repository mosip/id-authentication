package io.mosip.authentication.service.filter;

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

/**
 * The Class KycAuthFilter.
 * 
 * @author Sanjay Murali
 */
@Component
public class KycAuthFilter extends IdAuthFilter {

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
			if (Objects.nonNull(response)) {
				if (Objects.nonNull(publicKey)) {
					encryptKycResponse(response);
				} else {
					responseBody.put(RESPONSE, encode(toJsonString(response)));
				}
			}
			return responseBody;
		} catch (ClassCastException | JsonProcessingException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	private void encryptKycResponse(Map<String, Object> response) throws JsonProcessingException {
		byte[] symmetricDataEncrypt = null;
		byte[] asymmetricKeyEncrypt = null;
		if (Objects.nonNull(response)) {
			SecretKey symmetricKey = keyManager.getSymmetricKey();
			symmetricDataEncrypt = encryptor.symmetricEncrypt(symmetricKey, toJsonString(response).getBytes());
			asymmetricKeyEncrypt = encryptor.asymmetricPublicEncrypt(publicKey, symmetricKey.getEncoded());
		}

		if (Objects.nonNull(asymmetricKeyEncrypt) && Objects.nonNull(symmetricDataEncrypt)) {
			response.replace(RESPONSE, org.apache.commons.codec.binary.Base64.encodeBase64String(asymmetricKeyEncrypt)
					.concat(org.apache.commons.codec.binary.Base64.encodeBase64String(symmetricDataEncrypt)));
		}
	}

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

	@SuppressWarnings("unchecked")
	private Map<String, Object> constructKycInfo(Map<String, Object> identity) {
		return identity.entrySet().stream().filter(entry -> entry.getValue() instanceof List)
				.collect(Collectors.toMap(Entry::getKey, entry -> {
					List<Map<String, Object>> listOfMap = (List<Map<String, Object>>) entry.getValue();
					return listOfMap.stream()
							.map((Map<String, Object> map) -> map.entrySet().stream()
									.filter(innerEntry -> innerEntry.getValue() != null).collect(Collectors.toMap(
											Entry::getKey, Entry::getValue, (map1, map2) -> map1, LinkedHashMap::new)))
							.collect(Collectors.toList());

				}));

	}

	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}

}
