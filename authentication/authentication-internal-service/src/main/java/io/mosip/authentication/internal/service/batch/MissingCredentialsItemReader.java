package io.mosip.authentication.internal.service.batch;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.impl.idevent.CredentialStoreStatus;
import io.mosip.authentication.common.service.integration.CredentialRequestManager;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class MissingCredentialsItemReader.
 * @author Loganathan Sekar
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MissingCredentialsItemReader implements ItemReader<CredentialRequestIdsDto> {
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(MissingCredentialsItemReader.class);
	
	/** The total count. */
	private AtomicInteger totalCount;
	
	/** The effectivedtimes. */
	private String effectivedtimes;

	/** The credential event repo. */
	@Autowired
	private CredentialEventStoreRepository credentialEventRepo;

	/** The current page index. */
	private AtomicInteger currentPageIndex;
	
	/** The request ids iterator. */
	private Iterator<CredentialRequestIdsDto> requestIdsIterator;

	/** The credential request manager. */
	@Autowired
	private CredentialRequestManager credentialRequestManager;
	
	/**
	 * Initialize.
	 */
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
	 * Gets the next page items.
	 *
	 * @return the next page items
	 */
	private List<CredentialRequestIdsDto> getNextPageItems() {
		try {
			return credentialRequestManager.getMissingCredentialsPageItems(currentPageIndex.getAndIncrement(), effectivedtimes);
		} catch (RestServiceException | IDDataValidationException e) {
			mosipLogger.info(ExceptionUtils.getStackTrace(e));
		}
		return List.of();
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
	 * Gets the effective D times.
	 *
	 * @return the effective D times
	 */
	private String getEffectiveDTimes() {
		// Fetch credentials since last credential stored event date time.
		return DateUtils
				.formatToISOString(credentialEventRepo.findMaxCrDTimesByStatusCode(CredentialStoreStatus.STORED.name())
						.orElseGet(DateUtils::getUTCCurrentDateTime));
		
		// TODO code for debug.
//		return DateUtils
//				.formatToISOString(LocalDateTime.of(2021, 1, 1, 0, 0));
		
	}

}
