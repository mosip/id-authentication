package io.mosip.authentication.internal.service.batch;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.helper.RestHelperImpl;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class MissingCredentialsItemReader.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MissingCredentialsItemReader implements ItemReader<CredentialRequestIdsDto> {
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(MissingCredentialsItemReader.class);
	
	/** The Constant NO_RECORD_FOUND_ERR_CODE. */
	private static final String NO_RECORD_FOUND_ERR_CODE = "IDR-CRG-009";

	/** The rest helper. */
	@Autowired
	@Qualifier("external")
	private RestHelper restHelper;
	
	/** The rest request factory. */
	@Autowired
	private RestRequestFactory restRequestFactory;

	
	/** The current page index. */
	private AtomicInteger currentPageIndex;
	
	/** The total count. */
	private AtomicInteger totalCount;
	
	/** The effectivedtimes. */
	private String effectivedtimes;

	/** The credential event repo. */
	@Autowired
	private CredentialEventStoreRepository credentialEventRepo;

	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;

	/** The request ids iterator. */
	private Iterator<CredentialRequestIdsDto> requestIdsIterator;
	
	private void initialize() {
		currentPageIndex = new AtomicInteger(0);
		totalCount = new AtomicInteger(0);
		effectivedtimes = getEffectiveDTimes();
		requestIdsIterator = getRequestIdsIterator();
	}

	/**
	 * Gets the request ids iterator.
	 *
	 * @return the request ids iterator
	 */
	private Iterator<CredentialRequestIdsDto> getRequestIdsIterator() {
		Stream<CredentialRequestIdsDto> requestIdStream = Stream
				.<List<CredentialRequestIdsDto>>iterate(
						this.getNextPageItems(), 
						list -> list != null && !list.isEmpty(),
						list -> this.getNextPageItems())
				.flatMap(List::stream);
		return requestIdStream.iterator();
	}
	
	/**
	 * Read.
	 *
	 * @return the credential request ids dto
	 * @throws Exception the exception
	 * @throws UnexpectedInputException the unexpected input exception
	 * @throws ParseException the parse exception
	 * @throws NonTransientResourceException the non transient resource exception
	 */
	@Override
	public CredentialRequestIdsDto read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(requestIdsIterator == null) {
			initialize();
		}
		
		if (requestIdsIterator.hasNext()) {
			totalCount.incrementAndGet();
			return requestIdsIterator.next();
		} else {
			mosipLogger.info("Fetched missing credentials. Total count: {}", totalCount.get());
			requestIdsIterator = null;
			return null;
		}
	}


	/**
	 * Gets the next page items.
	 *
	 * @return the next page items
	 */
	private List<CredentialRequestIdsDto> getNextPageItems() {
		try {
			RestRequestDTO request = restRequestFactory.buildRequest(RestServicesConstants.CRED_REQUEST_GET_REQUEST_IDS, null, ResponseWrapper.class);
			Map<String, String> pathVariables = Map.of("pageNumber", String.valueOf(currentPageIndex.getAndIncrement()),
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
				}
			}
		} catch (IDDataValidationException e) {
			mosipLogger.error(ExceptionUtils.getStackTrace(e));
		}
		
		return List.of();
	}

	/**
	 * Gets the effective D times.
	 *
	 * @return the effective D times
	 */
	private String getEffectiveDTimes() {
		return DateUtils
				.formatToISOString(LocalDateTime.of(2021, 1, 1, 0, 0));
		//TODO commented for debug. Fetch credentials since last credential stored event date time.
//		return DateUtils
//				.formatToISOString(credentialEventRepo.findMaxCrDTimesByStatusCode(CredentialStoreStatus.STORED.name())
//						.orElseGet(DateUtils::getUTCCurrentDateTime));
	}

}
