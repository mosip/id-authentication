package io.mosip.authentication.service.filter;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class KycAuthFilter.
 * 
 * @author Sanjay Murali
 */
@Component
public class KycAuthFilter extends BaseAuthFilter {
	
	private static Logger mosipLogger = IdaLogger.getLogger(InternalAuthFilter.class);

	private static final String TXN_ID = "txnID";

	/** The Constant AUTH_REQUEST. */
	private static final String AUTH_REQUEST = "authRequest";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant RESPONSE. */
	private static final String RESPONSE = "response";

	/** The Constant AUTH. */
	private static final String AUTH = "auth";

	/** The Constant KYC. */
	private static final String KYC = "kyc";

	/** The Constant REQ_TIME. */
	private static final String REQ_TIME = "reqTime";

	/** The Constant RES_TIME. */
	private static final String RES_TIME = "resTime";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#decodedRequest(java.
	 * util.Map)
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	protected Map<String, Object> decodedRequest(Map<String, Object> requestBody) throws IdAuthenticationAppException {
		try {
			Map<String, Object> authRequest = (Map<String, Object>) decodeToMap((String) requestBody.get(AUTH_REQUEST));
			authRequest.replace(REQUEST, decode((String) authRequest.get(REQUEST)));
			if (null != authRequest.get(REQUEST)) {
				authRequest.replace(REQUEST, keyManager.requestData(authRequest, mapper));
			}
			requestBody.replace(AUTH_REQUEST, authRequest);
			return requestBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.service.filter.BaseAuthFilter#encodedResponse(java.
	 * util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, Object> encodedResponse(Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		try {
			Map<String, Object> response = (Map<String, Object>) responseBody.get(RESPONSE);
			if (response != null) {
				if (null != publicKey) {
					encryptKycResponse(response);
				} else {
					Object kyc = response.get(KYC);
					if (kyc != null) {
						response.replace(KYC, encode(toJsonString(kyc)));
					}
				}

				Object auth = response.get(AUTH);
				if (auth != null) {
					response.replace(AUTH, encode(toJsonString(auth)));
				}

				responseBody.replace(RESPONSE, encode(toJsonString(responseBody.get(RESPONSE))));
			}
			return responseBody;
		} catch (ClassCastException | JsonProcessingException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	private void encryptKycResponse(Map<String, Object> response) throws JsonProcessingException {
		Object kycDetail = response.get(KYC);
		byte[] symmetricDataEncrypt = null;
		byte[] asymmetricKeyEncrypt = null;
		if (kycDetail != null) {
			SecretKey symmetricKey = keyManager.getSymmetricKey();
			symmetricDataEncrypt = encryptor.symmetricEncrypt(symmetricKey, toJsonString(kycDetail).getBytes());
			asymmetricKeyEncrypt = encryptor.asymmetricPublicEncrypt(publicKey, symmetricKey.getEncoded());
		}

		if (null != asymmetricKeyEncrypt && null != symmetricDataEncrypt) {
			response.replace(KYC, org.apache.commons.codec.binary.Base64.encodeBase64String(asymmetricKeyEncrypt)
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
	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, Object> setResponseParam(Map<String, Object> requestBody, Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		try {
			Map<String, Object> authReq = (Map<String, Object>) requestBody.get(AUTH_REQUEST);
			if (Objects.nonNull(authReq) && Objects.nonNull(authReq.get(TXN_ID))) {
				responseBody.replace(TXN_ID, authReq.get(TXN_ID));
			}
			if (Objects.nonNull(authReq) && Objects.nonNull(authReq.get(REQ_TIME))
					&& isDate((String) authReq.get(REQ_TIME))) {
				ZoneId zone = ZonedDateTime.parse((CharSequence) authReq.get(REQ_TIME)).getZone();
				responseBody.replace(RES_TIME,
						DateUtils.formatDate(
								DateUtils.parseToDate((String) responseBody.get(RES_TIME),
										env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone)),
								env.getProperty(DATETIME_PATTERN), TimeZone.getTimeZone(zone)));
				return responseBody;
			} else {
				return responseBody;
			}
		} catch (DateTimeParseException e) {
			mosipLogger.error("sessionId", "IdAuthFilter", "setResponseParam", "\n" + ExceptionUtils.getStackTrace(e));
			return responseBody;
		}
	}

	protected Object decodeToMap(String stringToDecode) throws IdAuthenticationAppException {
		try {
			if (stringToDecode != null) {
				return mapper.readValue(Base64.getDecoder().decode(stringToDecode),
						new TypeReference<Map<String, Object>>() {
						});
			} else {
				return stringToDecode;
			}
		} catch (IllegalArgumentException | IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
	}

	@Override
	protected boolean validateSignature(String signature, byte[] requestAsByte) throws IdAuthenticationAppException {
		return true;
	}

}
