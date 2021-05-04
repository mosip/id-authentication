package io.mosip.authentication.common.service.integration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.helper.RestHelperImpl;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthRetryException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.retry.WithRetry;

/**
 * The Class CredentialRequestManager.
 * @author Loganathan Sekar
 */
@Component
public class CredentialRequestManager {
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(CredentialRequestManager.class);
	
	/** The Constant NO_RECORD_FOUND_ERR_CODE. */
	private static final String NO_RECORD_FOUND_ERR_CODE = "IDR-CRG-009";

	/** The rest helper. */
	@Autowired
	@Qualifier("external")
	private RestHelper restHelper;
	
	/** The rest request factory. */
	@Autowired
	private RestRequestFactory restRequestFactory;

	
	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;
	
	/**
	 * Gets the next page items.
	 *
	 * @param currentPageIndex the current page index
	 * @param effectivedtimes the effectivedtimes
	 * @return the next page items
	 */
	@WithRetry
	public List<CredentialRequestIdsDto> getMissingCredentialsPageItems(int currentPageIndex, String effectivedtimes) {
		try {
			RestRequestDTO request = restRequestFactory.buildRequest(RestServicesConstants.CRED_REQUEST_GET_REQUEST_IDS, null, ResponseWrapper.class);
			Map<String, String> pathVariables = Map.of("pageNumber", String.valueOf(currentPageIndex),
														"effectivedtimes", effectivedtimes);
			request.setPathVariables(pathVariables);
			
			try {
				Map<String, Object> response = restHelper.<ResponseWrapper<Map<String, Object>>>requestSync(request).getResponse();
				List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
				if (data == null) {
					return List.of();
				} else {
					List<CredentialRequestIdsDto> requestIds = data.stream().map(map -> objectMapper.convertValue(map, CredentialRequestIdsDto.class))
							.collect(Collectors.toList());
					return requestIds;
				}
			} catch (RestServiceException e) {
				List<ServiceError> errorList = RestHelperImpl.getErrorList(e.getResponseBodyAsString().orElse("{}"), objectMapper);
				if(errorList.stream().anyMatch(err -> NO_RECORD_FOUND_ERR_CODE.equals(err.getErrorCode()))) {
					//Reached end of pages.
					return List.of();
				} else {
					mosipLogger.error(ExceptionUtils.getStackTrace(e));
					throw new IdAuthRetryException(e);
				}
			}
		} catch (IDDataValidationException e) {
			mosipLogger.error(ExceptionUtils.getStackTrace(e));
			throw new IdAuthRetryException(e);
		}
	}
	
	/**
	 * Retrigger credential issuance.
	 *
	 * @param requestId the request id
	 */
	public void retriggerCredentialIssuance(String requestId) {
		try {
			RestRequestDTO request = restRequestFactory.buildRequest(
					RestServicesConstants.CRED_REQUEST_RETRIGGER_CRED_ISSUANCE, null, ResponseWrapper.class);
			Map<String, String> pathVariables = Map.of("requestId", requestId);
			request.setPathVariables(pathVariables);

			try {
				restHelper.<ResponseWrapper<Map<String, Object>>>requestSync(request).getResponse();
			} catch (RestServiceException e) {
				mosipLogger.error(ExceptionUtils.getStackTrace(e));
				throw new IdAuthRetryException(e);
			}
		} catch (IDDataValidationException e) {
			mosipLogger.error(ExceptionUtils.getStackTrace(e));
			throw new IdAuthRetryException(e);
		}
	}

}
