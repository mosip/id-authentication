package io.mosip.authentication.internal.service.batch;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.lang.exception.ExceptionUtils;
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
import io.mosip.idrepository.core.dto.PageDto;
import io.mosip.kernel.core.exception.ServiceError;
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
	private AtomicInteger currentPageIndex = new AtomicInteger(0);
	
	/** The effectivedtimes. */
	private String effectivedtimes = getEffectiveDTimes();

	/** The credential event repo. */
	@Autowired
	private CredentialEventStoreRepository credentialEventRepo;

	/** The object mapper. */
	@Autowired

	private ObjectMapper objectMapper;

	/** The request ids iterator. */
	private Iterator<CredentialRequestIdsDto> requestIdsIterator;

	/**
	 * Gets the request ids iterator.
	 *
	 * @return the request ids iterator
	 */
	private Iterator<CredentialRequestIdsDto> getRequestIdsIterator() {
		Stream<CredentialRequestIdsDto> requestIdStream = Stream
				.<List<CredentialRequestIdsDto>>iterate(this.getNextPageItems(), list -> !list.isEmpty(),
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
			requestIdsIterator = getRequestIdsIterator();
		}
		return requestIdsIterator.hasNext() ? requestIdsIterator.next() : null;
	}

	/**
	 * Gets the next page items.
	 *
	 * @return the next page items
	 */
	private List<CredentialRequestIdsDto> getNextPageItems() {
		try {
			RestRequestDTO request = restRequestFactory.buildRequest(RestServicesConstants.CRED_REQUEST_GET_REQUEST_IDS, null, PageDto.class);
			Map<String, String> pathVariables = Map.of("pageNumber", String.valueOf(currentPageIndex.getAndIncrement()),
														"effectivedtimes", effectivedtimes);
			request.setPathVariables(pathVariables);
			
			try {
				return restHelper.<PageDto<CredentialRequestIdsDto>>requestSync(request).getData();
			} catch (RestServiceException e) {
				List<ServiceError> errorList = RestHelperImpl.getErrorList(e.getResponseBodyAsString().orElse("{}"), objectMapper);
				if(errorList.stream().anyMatch(err -> NO_RECORD_FOUND_ERR_CODE.equals(err.getErrorCode()))) {
					//Reached end of pages
					return List.of();
				} else {
					mosipLogger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
		} catch (IDDataValidationException e) {
			mosipLogger.error(ExceptionUtils.getFullStackTrace(e));
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
				.formatToISOString(credentialEventRepo.getMaxCrDTimes()
						.orElseGet(DateUtils::getUTCCurrentDateTime));
	}

}
