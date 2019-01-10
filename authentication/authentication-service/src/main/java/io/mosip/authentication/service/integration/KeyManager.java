package io.mosip.authentication.service.integration;

import java.io.IOException;
import java.time.LocalDateTime;
//import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.dto.CryptomanagerRequestDto;
import io.mosip.authentication.service.integration.dto.CryptomanagerResponseDto;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class KeyManager.
 * 
 * @author Sanjay Murali
 */
@Component
public class KeyManager {

	/** The Constant SESSION_KEY. */
	private static final String SESSION_KEY = "sessionKey";

	/** The Constant KEY. */
	private static final String KEY = "key";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant TSP_ID. */
	private static final String TSP_ID = "tspID";

	/** KeySplitter. */

	@Value("${mosip.kernel.data-key-splitter}")
	private String keySplitter;

	/** The app id. */
	@Value("${application.id}")
	private String appId;

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The rest request factory. */
	@Autowired
	private RestRequestFactory restRequestFactory;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(KeyManager.class);

	/**
	 * Request data.
	 *
	 * @param requestBody
	 *            the request body
	 * @param mapper
	 *            the mapper
	 * @return the map
	 * @throws IdAuthenticationAppException
	 *             the id authentication app exception
	 */
	public Map<String, Object> requestData(Map<String, Object> requestBody, ObjectMapper mapper)
			throws IdAuthenticationAppException {
		Map<String, Object> request = null;
		try {
			String tspId = (String) requestBody.get(TSP_ID);
			if (Objects.isNull(tspId) || tspId.isEmpty()) {
				logger.error("NA", "NA", IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				throw new IdAuthenticationAppException(
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			}
			byte[] encryptedRequest = (byte[]) requestBody.get(REQUEST);
			Optional<String> encryptedSessionKey = Optional.ofNullable(requestBody.get(KEY))// check if key is null
					.filter(obj -> obj instanceof Map)
					.map(obj -> String.valueOf(((Map<String, Object>) obj).get(SESSION_KEY)));
			if (encryptedSessionKey.isPresent()) {
				byte[] encyptedSessionkey = Base64.decodeBase64(encryptedSessionKey.get());// remove key attribute from
																							// auth request
				RestRequestDTO restRequestDTO = null;
				CryptomanagerRequestDto cryptoManagerRequestDto = new CryptomanagerRequestDto();
				CryptomanagerResponseDto cryptomanagerResponseDto = null;
				String decryptedData = null;

				try {
					cryptoManagerRequestDto.setApplicationId(appId);
					cryptoManagerRequestDto.setReferenceId(tspId);
					LocalDateTime utcTime = DateUtils.getUTCCurrentDateTime();
					cryptoManagerRequestDto.setTimeStamp(utcTime);
					cryptoManagerRequestDto.setData(CryptoUtil.encodeBase64(
							CryptoUtil.combineByteArray(encryptedRequest, encyptedSessionkey, keySplitter)));
					restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.DECRYPTION_SERVICE,
							cryptoManagerRequestDto, CryptomanagerResponseDto.class);
					cryptomanagerResponseDto = restHelper.requestSync(restRequestDTO);
					decryptedData = new String(Base64.decodeBase64(cryptomanagerResponseDto.getData()));
					logger.info("NA", "NA", "NA", "cryptomanagerResponseDto " + decryptedData);
				} catch (RestServiceException e) {
					logger.error("NA", "NA", e.getErrorCode(), e.getErrorText());
					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.SERVER_ERROR);
				} catch (IDDataValidationException e) {
					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST, e);
				}

				request = mapper.readValue(decryptedData, new TypeReference<Map<String, Object>>() {
				});
			}
		} catch (IOException e) {
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
		}
		return request;
	}

}
