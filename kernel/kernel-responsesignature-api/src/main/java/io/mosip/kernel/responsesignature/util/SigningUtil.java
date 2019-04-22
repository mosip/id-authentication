package io.mosip.kernel.responsesignature.util;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.signatureutil.exception.ParseResponseException;
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilClientException;
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.responsesignature.constant.SignatureUtilConstant;
import io.mosip.kernel.responsesignature.constant.SigningDataErrorCode;
import io.mosip.kernel.responsesignature.dto.CryptoManagerRequestDto;
import io.mosip.kernel.responsesignature.dto.CryptoManagerResponseDto;


/**
 * SigningUtil class.
 *
 * @author Srinivasan
 * @since 1.0.0
 */
@Component
public class SigningUtil {

	/** The rest template. */
	@Autowired
	RestTemplate restTemplate;

	/** The sync data request id. */
	@Value("${mosip.kernel.signature.signature-request-id}")
	private String syncDataRequestId;

	/** The sync data version id. */
	@Value("${mosip.kernel.signature.signature-version-id}")
	private String syncDataVersionId;

	/** The encrypt url. */
	@Value("${mosip.kernel.signature.cryptomanager-encrypt-url}")
	private String encryptUrl;

	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * This util will get the raw data as input and will hash the data. The data
	 * then is signed with private key.
	 *
	 * @param response
	 *            the response
	 * @return {@link String} digest as plain text 
	 */
	public String signResponseData(String response) {

		byte[] responseByteArray = HMACUtils.generateHash(response.getBytes());
        CryptoManagerRequestDto cryptoManagerRequestDto = new CryptoManagerRequestDto();
		cryptoManagerRequestDto.setApplicationId(SignatureUtilConstant.APPLICATION_ID);
		cryptoManagerRequestDto.setReferenceId(SignatureUtilConstant.REFERENCE_ID);
		cryptoManagerRequestDto.setData(CryptoUtil.encodeBase64(responseByteArray));
		cryptoManagerRequestDto.setTimeStamp(DateUtils.getUTCCurrentDateTimeString());
		RequestWrapper<CryptoManagerRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId(syncDataRequestId);
		requestWrapper.setVersion(syncDataVersionId);
		requestWrapper.setRequest(cryptoManagerRequestDto);
		ResponseEntity<String> responseEntity = null;

		try {
			responseEntity = restTemplate.postForEntity(encryptUrl, requestWrapper, String.class);
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());

			if (!validationErrorsList.isEmpty()) {
				throw new SignatureUtilClientException(validationErrorsList);
			} else {
				throw new SignatureUtilException(SigningDataErrorCode.REST_CLIENT_EXCEPTION.getErrorCode(),
						SigningDataErrorCode.REST_CLIENT_EXCEPTION.getErrorMessage());
			}
		}
		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseEntity.getBody());

		if (!validationErrorsList.isEmpty()) {
			throw new SignatureUtilClientException(validationErrorsList);
		}
		CryptoManagerResponseDto cryptoManagerResponseDto = null;
		ResponseWrapper<CryptoManagerResponseDto> responseObject;
		try {

			responseObject = objectMapper.readValue(responseEntity.getBody(),
					new TypeReference<ResponseWrapper<CryptoManagerResponseDto>>() {
					});

			cryptoManagerResponseDto = responseObject.getResponse();
		} catch (IOException | NullPointerException exception) {
			throw new ParseResponseException(SigningDataErrorCode.RESPONSE_PARSE_EXCEPTION.getErrorCode(),
					SigningDataErrorCode.RESPONSE_PARSE_EXCEPTION.getErrorMessage());
		}

		return cryptoManagerResponseDto.getData();

	}

}
