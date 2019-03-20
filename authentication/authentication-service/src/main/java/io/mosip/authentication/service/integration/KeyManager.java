package io.mosip.authentication.service.integration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
//import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
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
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

/**
 * The Class KeyManager.
 * 
 * @author Sanjay Murali
 */
@Component
public class KeyManager {

	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant SESSION_KEY. */
	private static final String SESSION_KEY = "requestSessionKey";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

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

	@Autowired
	private KeyGenerator keyGenerator;

	@Autowired
	private Environment environment;

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(KeyManager.class);

	/**
	 * Request data.
	 *
	 * @param requestBody the request body
	 * @param mapper      the mapper
	 * @return the map
	 * @throws IdAuthenticationAppException      the id authentication app exception
	 * @throws IdAuthenticationBusinessException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> requestData(Map<String, Object> requestBody, ObjectMapper mapper)
			throws IdAuthenticationAppException {
		Map<String, Object> request = null;
		try {
			byte[] encryptedRequest = (byte[]) requestBody.get(REQUEST);
			Optional<String> encryptedSessionKey = Optional.ofNullable(requestBody.get(SESSION_KEY))
					.map(String::valueOf);
			if (encryptedSessionKey.isPresent()) {
				byte[] encyptedSessionkey = Base64.decodeBase64(encryptedSessionKey.get());// remove key attribute from
				RestRequestDTO restRequestDTO = null;
				CryptomanagerRequestDto cryptoManagerRequestDto = new CryptomanagerRequestDto();
				CryptomanagerResponseDto cryptomanagerResponseDto = null;
				String decryptedData = null;
				try {
					cryptoManagerRequestDto.setApplicationId(appId);
					cryptoManagerRequestDto.setReferenceId(environment.getProperty("mosip.ida.publickey"));
					cryptoManagerRequestDto.setTimeStamp(
							DateUtils.getUTCCurrentDateTimeString(environment.getProperty("datetime.pattern")));
					// cryptoManagerRequestDto.setTimeStamp("2031-03-07T12:58:41.762Z");
					cryptoManagerRequestDto.setData(CryptoUtil.encodeBase64(
							CryptoUtil.combineByteArray(encryptedRequest, encyptedSessionkey, keySplitter)));
					restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.DECRYPTION_SERVICE,
							cryptoManagerRequestDto, CryptomanagerResponseDto.class);
					cryptomanagerResponseDto = restHelper.requestSync(restRequestDTO);
					decryptedData = new String(Base64.decodeBase64(cryptomanagerResponseDto.getData()),
							StandardCharsets.UTF_8);
					logger.info(SESSION_ID, this.getClass().getSimpleName(), "requestData",
							"cryptomanagerResponseDto " + decryptedData);
				} catch (RestServiceException e) {
					logger.error(SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
					Optional<Object> responseBody = e.getResponseBody();
					if (responseBody.isPresent()) {
						Map<String, Object> idrepoMap = (Map<String, Object>) responseBody.get();
						if (idrepoMap.containsKey("errors")) {
							List<Map<String, Object>> idRepoerrorList = (List<Map<String, Object>>) idrepoMap
									.get("errors");
							String keyExpErrorCode = "KER-KMS-003"; // TODO FIXME integrate with kernel error constant
							if (!idRepoerrorList.isEmpty()
									&& idRepoerrorList.stream().anyMatch(map -> map.containsKey("errCode")
											&& ((String) map.get("errCode")).equalsIgnoreCase(keyExpErrorCode))) {
								throw new IdAuthenticationAppException(
										IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED);
							} else {
								throw new IdAuthenticationAppException(
										IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
							}
						}
					}

					logger.error(SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.SERVER_ERROR);
				} catch (IDDataValidationException e) {
					logger.error(SESSION_ID, this.getClass().getSimpleName(), e.getErrorCode(), e.getErrorText());
					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
				}

				request = mapper.readValue(decryptedData.getBytes("UTF-8"),Map.class);
			}
		} catch (IOException e) {
			logger.error(SESSION_ID, this.getClass().getSimpleName(), "requestData", e.getMessage());
			throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
		return request;
	}

	public SecretKey getSymmetricKey() {
		return keyGenerator.getSymmetricKey();
	}

}
