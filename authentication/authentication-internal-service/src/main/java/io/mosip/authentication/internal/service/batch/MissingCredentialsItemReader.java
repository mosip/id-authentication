package io.mosip.authentication.internal.service.batch;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_MAX_CREDENTIAL_PULL_WINDOW_DAYS;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.impl.idevent.CredentialStoreStatus;
import io.mosip.authentication.common.service.integration.CredentialRequestManager;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthRetryException;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class MissingCredentialsItemReader.
 * @author Loganathan Sekar
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MissingCredentialsItemReader implements ItemReader<CredentialRequestIdsDto> {
	
	private static final int DEFAULT_MAX_CREDENTIAL_PULL_WINDOW_DAYS = 2;

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

	@Value("${" + IDA_MAX_CREDENTIAL_PULL_WINDOW_DAYS + ":" + DEFAULT_MAX_CREDENTIAL_PULL_WINDOW_DAYS + "}" )
	private int maxCredentialPullWindowDays;
	
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
			List<CredentialRequestIdsDto> credRequests = credentialRequestManager.getMissingCredentialsPageItems(currentPageIndex.getAndIncrement(), effectivedtimes);
			// Filter out any request ID which is already stored credential event store.
			// Such entry could be there due to the Pull Failed Message Job has processed the respective
			// CREDENTIAL_ISSUED event.
			List<String> requestIds = credRequests.stream()
					.map(CredentialRequestIdsDto::getRequestId)
					.collect(Collectors.toList());
			if(!requestIds.isEmpty()) {
				List<String> existingEvents = credentialEventRepo.findDistictCredentialTransactionIdsInList(requestIds);
				return credRequests.stream()
						.filter(requestIdDto -> !existingEvents.contains(requestIdDto.getRequestId()))
						.collect(Collectors.toList());
			} else {
				return List.of();
			}
		} catch (RestServiceException | IDDataValidationException e) {
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,e);
		}
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
	public CredentialRequestIdsDto read() throws Exception {
		try {
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
		} catch (IdAuthUncheckedException e) {
			// Throwing retry exception to perform job level retry
			throw new IdAuthRetryException(e);
		} catch (Exception e) {
			// Throwing retry exception to perform job level retry
			throw new IdAuthRetryException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), 
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage(), e);
		}
	}


	/**
	 * Gets the effective D times.
	 *
	 * @return the effective D times
	 */
	private String getEffectiveDTimes() {
		// Fetch credentials since last credential stored event date time.
		Optional<LocalDateTime> lastCredentialStoreTime = credentialEventRepo.findMaxCrDTimesByStatusCode(CredentialStoreStatus.STORED.name());
		LocalDateTime maxCredentialPullWindowTime = LocalDateTime.now().minus(maxCredentialPullWindowDays, ChronoUnit.DAYS);
		
		LocalDateTime effectiveDTimes = lastCredentialStoreTime.orElse(maxCredentialPullWindowTime);
		//If last credential store time is greater than window time, use the window time
		if(effectiveDTimes.isBefore(maxCredentialPullWindowTime)) {
			effectiveDTimes = maxCredentialPullWindowTime;
		}
		
		return DateUtils.formatToISOString(effectiveDTimes);
		
	}

}
