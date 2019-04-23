package io.mosip.kernel.syncdata.utils;

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
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.syncdata.constant.SigningDataErrorCode;
import io.mosip.kernel.syncdata.dto.CryptoManagerRequestDto;
import io.mosip.kernel.syncdata.dto.CryptoManagerResponseDto;
import io.mosip.kernel.syncdata.exception.CryptoManagerServiceException;
import io.mosip.kernel.syncdata.exception.ParseResponseException;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;

/**
 * 
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@Component
public class SigningUtil {

	@Autowired
	RestTemplate restTemplate;

	@Value("${mosip.kernel.syncdata.syncdata-request-id:SYNCDATA.REQUEST}")
	private String syncDataRequestId;

	@Value("${mosip.kernel.syncdata.syncdata-version-id:v1.0}")
	private String syncDataVersionId;

	@Value("${mosip.kernel.syncdata.cryptomanager-encrypt-url}")
	private String encryptUrl;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String APPLICATION_ID = "KERNEL";

	private static final String REFERENCE_ID = "KER";

	/**
	 * This util will get the raw data as input and will hash the data. The data
	 * then signed with private key.
	 *
	 * @param response
	 *            the response
	 * @return digestasplainText {@link String}
	 */
	public String signResponseData(String response) {

		byte[] responseByteArray = HMACUtils.generateHash(response.getBytes());

		CryptoManagerRequestDto cryptoManagerRequestDto = new CryptoManagerRequestDto();
		cryptoManagerRequestDto.setApplicationId(APPLICATION_ID);
		cryptoManagerRequestDto.setReferenceId(REFERENCE_ID);
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
				throw new CryptoManagerServiceException(validationErrorsList);
			} else {
				throw new SyncDataServiceException(SigningDataErrorCode.REST_CLIENT_EXCEPTION.getErrorCode(),
						SigningDataErrorCode.REST_CLIENT_EXCEPTION.getErrorMessage());
			}
		}
		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseEntity.getBody());

		if (!validationErrorsList.isEmpty()) {
			throw new CryptoManagerServiceException(validationErrorsList);
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
