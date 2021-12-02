package io.mosip.authentication.common.service.impl.idevent;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.IDA;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_RETRY_BACKOFF_EXPONENTIAL_MAX_INTERVAL_MILLISECS;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_RETRY_BACKOFF_EXPONENTIAL_MULTIPLIER;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_RETRY_BACKOFF_INTERVAL_MILLISECS;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.CREDENTIAL_STORE_RETRY_MAX_LIMIT;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.authentication.common.service.entity.IdaUinHashSalt;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.integration.CredentialRequestManager;
import io.mosip.authentication.common.service.integration.DataShareManager;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.websub.impl.CredentialStoreStatusEventPublisher;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthRetryException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RetryingBeforeRetryIntervalException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.dto.CredentialRequestIdsDto;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;

/**
 * The CredentialStoreServiceImpl - the service to store credentials in IDA DB from
 * the credential issuance event.
 *
 * @author Loganathan Sekar
 */
@Component
public class CredentialStoreServiceImpl implements CredentialStoreService {

	/** The Constant BIO_KEY. */
	private static final String BIO_KEY = "bioEncryptedRandomKey";

	/** The Constant BIO_KEY_INDEX. */
	private static final String BIO_KEY_INDEX = "bioRankomKeyIndex";

	/** The Constant DEMO_KEY. */
	private static final String DEMO_KEY = "demoEncryptedRandomKey";

	/** The Constant DEMO_KEY_INDEX. */
	private static final String DEMO_KEY_INDEX = "demoRankomKeyIndex";

	/** The Constant TOKEN. */
	private static final String TOKEN = "TOKEN";

	/** The Constant EXPIRY_TIME. */
	private static final String EXPIRY_TIME = "expiry_timestamp";

	/** The Constant TRANSACTION_LIMIT. */
	private static final String TRANSACTION_LIMIT = "transaction_limit";

	/** The Constant SALT. */
	private static final String SALT = "SALT";

	/** The Constant MODULO. */
	private static final String MODULO = "MODULO";

	/** The Constant ID_HASH. */
	public static final String ID_HASH = "id_hash";

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(CredentialStoreServiceImpl.class);

	/** The identity cache repo. */
	@Autowired
	private IdentityCacheRepository identityCacheRepo;

	/** The audit helper. */
	@Autowired
	private AuditHelper auditHelper;

	/** The data share manager. */
	@Autowired
	private DataShareManager dataShareManager;

	/** The uin hash salt repo. */
	@Autowired
	private IdaUinHashSaltRepo uinHashSaltRepo;

	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;

	/** The security manager. */
	@Autowired
	private IdAuthSecurityManager securityManager;

	/** The max retry count. */
	@Value("${" + CREDENTIAL_STORE_RETRY_MAX_LIMIT + ":20}")
	private int maxRetryCount;

	/** The retry interval. */
	@Value("${" + CREDENTIAL_STORE_RETRY_BACKOFF_INTERVAL_MILLISECS + ":60000}")
	private long retryInterval;

	/** The credential event repo. */
	@Autowired
	private CredentialEventStoreRepository credentialEventRepo;

	/** The interval exponential multiplier. Default value is 1 - resulting in fixed backoff retry inteval */
	@Value("${" + CREDENTIAL_STORE_RETRY_BACKOFF_EXPONENTIAL_MULTIPLIER + ":1}")
	private double intervalExponentialMultiplier;

	/** The max exponential retry interval limit millis. Default value is set to 1 hour*/
	@Value("${" + CREDENTIAL_STORE_RETRY_BACKOFF_EXPONENTIAL_MAX_INTERVAL_MILLISECS + ":3600000}")
	private long maxExponentialRetryIntervalLimitMillis;
	
	/** The credential store status event publisher. */
	@Autowired
	private CredentialStoreStatusEventPublisher credentialStoreStatusEventPublisher;
	
	/** The credential request manager. */
	@Autowired
	private CredentialRequestManager credentialRequestManager;
	
	/**
	 * Process credential store event.
	 *
	 * @param credentialEventStore the credential event store
	 * @return the identity entity
	 * @throws IdAuthenticationBusinessException    the id authentication business
	 *                                              exception
	 * @throws RetryingBeforeRetryIntervalException the retrying before retry
	 *                                              interval exception
	 */
	@Override
	public IdentityEntity processCredentialStoreEvent(CredentialEventStore credentialEventStore)
			throws IdAuthenticationBusinessException, RetryingBeforeRetryIntervalException {
		String statusCode = credentialEventStore.getStatusCode();
		if (statusCode.equals(CredentialStoreStatus.FAILED.name())) {
			skipIfWaitingForRetryInterval(credentialEventStore);
		}
		
		try {
			IdentityEntity entity = doProcessCredentialStoreEvent(credentialEventStore);
			updateEventProcessingStatus(credentialEventStore, true, false, statusCode);
			return entity;
		} catch (RuntimeException e) {
			// Any Runtime exception is marked as non-recoverable and hence retry is skipped for that
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					"processCredentialStoreEvent", "Error in Processing credential store event: " + e.getMessage());
			updateEventProcessingStatus(credentialEventStore, false, false, statusCode);
			throw e;
		} catch (Exception e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					"processCredentialStoreEvent", "Error in Processing credential store event: " + e.getMessage());
			updateEventProcessingStatus(credentialEventStore, false, true, statusCode);
			throw e;
		}
	}

	/**
	 * Skip if waiting for retry interval.
	 *
	 * @param credentialEventStore the upd D times
	 * @throws RetryingBeforeRetryIntervalException the retrying before retry
	 *                                              interval exception
	 */
	private void skipIfWaitingForRetryInterval(CredentialEventStore credentialEventStore) throws RetryingBeforeRetryIntervalException {
		Assert.isTrue(intervalExponentialMultiplier >= 1, CREDENTIAL_STORE_RETRY_BACKOFF_EXPONENTIAL_MULTIPLIER + " property value should be greater than or equal to 1.");
		
		long backoffIntervalMillis = (long) (retryInterval * Math.pow(intervalExponentialMultiplier, credentialEventStore.getRetryCount()));
		if(backoffIntervalMillis > maxExponentialRetryIntervalLimitMillis) {
			backoffIntervalMillis = maxExponentialRetryIntervalLimitMillis;
		}
		
		LocalDateTime updateDtimes = credentialEventStore.getUpdDTimes();
		if (DateUtils.getUTCCurrentDateTime().isBefore(updateDtimes.plus(backoffIntervalMillis, ChronoUnit.MILLIS))) {
			throw new RetryingBeforeRetryIntervalException();
		}
	}

	/**
	 * Update event processing status.
	 *
	 * @param credentialEventStore the credential event store
	 * @param isSuccess            the is success
	 * @param isRecoverableException the is recoverable exception
	 * @param status the status
	 */
	@Transactional
	private void updateEventProcessingStatus(CredentialEventStore credentialEventStore, boolean isSuccess, boolean isRecoverableException,
			String status) {
		credentialEventStore.setUpdBy(IDA);
		LocalDateTime updatedDTimes = DateUtils.getUTCCurrentDateTime();
		credentialEventStore.setUpdDTimes(updatedDTimes);
		String requestId = credentialEventStore.getCredentialTransactionId();

		if (isSuccess) {
			String statusCode = CredentialStoreStatus.STORED.name();
			credentialEventStore.setStatusCode(statusCode);
			// Send websub event "STORED" status for the event id.
			credentialStoreStatusEventPublisher.publishEvent(statusCode, requestId, updatedDTimes);
			audit(requestId, statusCode);
		} else {
			if (isRecoverableException) {
				int retryCount;
				if (status.equals(CredentialStoreStatus.NEW.name()) || status.equals(CredentialStoreStatus.FAILED.name())) {
					retryCount = credentialEventStore.getRetryCount() + 1;
					if (retryCount < maxRetryCount) {
						if(status.equals(CredentialStoreStatus.NEW.name())) {
							updateStatusAndRetryCount(credentialEventStore, Optional.of(CredentialStoreStatus.FAILED.name()), OptionalInt.empty());
						} else if(status.equals(CredentialStoreStatus.FAILED.name())){
							updateStatusAndRetryCount(credentialEventStore, Optional.empty(), OptionalInt.of(retryCount));
						}
					} else {
						retryCount = maxRetryCount;
						updateStatusAndRetryCount(credentialEventStore, Optional.of(CredentialStoreStatus.FAILED_WITH_MAX_RETRIES.name()), OptionalInt.of(retryCount));
						
						// Send websub event failure message for the event id. For all failures we will send "FAILED" status only
						credentialStoreStatusEventPublisher.publishEvent(CredentialStoreStatus.FAILED.name(), requestId, updatedDTimes);
						audit(requestId, CredentialStoreStatus.FAILED.name());
					}
				}
			} else {
				// Any Runtime exception is marked as non-recoverable and hence retry is skipped for that
				updateStatusAndRetryCount(credentialEventStore, Optional.of(CredentialStoreStatus.FAILED_NON_RECOVERABLE.name()), OptionalInt.empty());
				
				// Send websub event failure message for the event id. For all failures we will send "FAILED" status only
				credentialStoreStatusEventPublisher.publishEvent(CredentialStoreStatus.FAILED.name(), requestId, updatedDTimes);
				audit(requestId, CredentialStoreStatus.FAILED.name());
			}
		}
	}

	private void updateStatusAndRetryCount(CredentialEventStore credentialEventStore, Optional<String> status, OptionalInt retryCount) {
		retryCount.ifPresent(credentialEventStore::setRetryCount);
		status.ifPresent(credentialEventStore::setStatusCode);
		
		credentialEventStore.setUpdBy(IDA);
		credentialEventStore.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
		
		credentialEventRepo.save(credentialEventStore);
	}

	/**
	 * Audit.
	 *
	 * @param requestId the request id
	 * @param desc the desc
	 */
	private void audit(String requestId, String desc) {
		try {
			auditHelper.audit(AuditModules.CREDENTIAL_STORAGE, AuditEvents.CREDENTIAL_STORED_EVENT, requestId, "request-id", desc);
		} catch (IDDataValidationException e) {
			mosipLogger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}

	/**
	 * Store event model.
	 *
	 * @param eventModel the event model
	 */
	@Override
	public void storeEventModel(EventModel eventModel) {
		CredentialEventStore credentialEvent = new CredentialEventStore();
		credentialEvent.setCrBy(IDA);
		credentialEvent.setCrDTimes(DateUtils.getUTCCurrentDateTime());
		credentialEvent.setEventId(eventModel.getEvent().getId());
		credentialEvent.setEventTopic(eventModel.getTopic());
		credentialEvent.setCredentialTransactionId(eventModel.getEvent().getTransactionId());
		credentialEvent.setPublishedOnDtimes(DateUtils.convertUTCToLocalDateTime(eventModel.getPublishedOn()));
		credentialEvent.setPublisher(eventModel.getPublisher());
		credentialEvent.setStatusCode(CredentialStoreStatus.NEW.name());
		credentialEvent.setRetryCount(0);
		try {
			credentialEvent.setEventObject(objectMapper.writeValueAsString(eventModel));
			credentialEventRepo.save(credentialEvent);
		} catch (JsonProcessingException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), "storeEventModel",
					"error in json processing: " + e.getMessage());
		}
	}

	/**
	 * Do process credential store event.
	 *
	 * @param credentialEventStore the credential event store
	 * @return the identity entity
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Transactional
	private IdentityEntity doProcessCredentialStoreEvent(CredentialEventStore credentialEventStore)
			throws IdAuthenticationBusinessException {
		
		String eventObjectStr = credentialEventStore.getEventObject();
		try {
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), "processCredentialStoreEvent",
					"Processing credential store event: " + objectMapper.writeValueAsString(credentialEventStore));
			
			EventModel eventModel = objectMapper.readValue(eventObjectStr.getBytes(), EventModel.class);
			Event event = eventModel.getEvent();

			String dataShareUri = event.getDataShareUri();
			if (dataShareUri != null) {
				try {
					Map<String, Object> additionalData = event.getData();
					String modulo = (String) additionalData.get(MODULO);
					String salt = (String) additionalData.get(SALT);
					String demoKeyIndex = (String) additionalData.get(DEMO_KEY_INDEX);
					String demoKey = (String) additionalData.get(DEMO_KEY);
					String bioKeyIndex = (String) additionalData.get(BIO_KEY_INDEX);
					String bioKey = (String) additionalData.get(BIO_KEY);

					saveSalt(modulo, salt);

					if (demoKeyIndex != null && demoKey != null) {
						securityManager.reEncryptAndStoreRandomKey(demoKeyIndex, demoKey);
					}

					if (bioKeyIndex != null && bioKey != null) {
						securityManager.reEncryptAndStoreRandomKey(bioKeyIndex, bioKey);
					}

					String idHash = (String) additionalData.get(ID_HASH);
					Integer transactionLimit = (Integer) additionalData.get(TRANSACTION_LIMIT);
					String expiryTime = (String) additionalData.get(EXPIRY_TIME);
					String token = (String) additionalData.get(TOKEN);
					Map<String, Object> credentialData = dataShareManager.downloadObject(dataShareUri, Map.class, true);
					return createIdentityEntity(idHash, token, transactionLimit, expiryTime, credentialData);

				} catch (RestServiceException | IDDataValidationException e) {
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
				}
			} else {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage()
								+ ": Data Share URI is not proivded in the event");
			}
		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), "retrieveAndStoreCredential",
					"Error parsing event model: " + eventObjectStr);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage()
							+ ": Error parsing event model: ");
		}
	}

	/**
	 * Creates the identity entity.
	 *
	 * @param idHash           the id hash
	 * @param token            the token
	 * @param transactionLimit the transaction limit
	 * @param expiryTime       the expiry time
	 * @param credentialData   the credential data
	 * @return the identity entity
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@SuppressWarnings("unchecked")
	private IdentityEntity createIdentityEntity(String idHash, String token, Integer transactionLimit,
			String expiryTime, Map<String, Object> credentialData) throws IdAuthenticationBusinessException {
		Map<String, Object>[] demoBioData = splitDemoBioData(
				(Map<String, Object>) credentialData.get(IdAuthCommonConstants.CREDENTIAL_SUBJECT));
		try {
			byte[] demoBytes = objectMapper.writeValueAsBytes(demoBioData[0]);
			byte[] bioBytes = objectMapper.writeValueAsBytes(demoBioData[1]);

			IdentityEntity identityEntity = new IdentityEntity();
			Optional<IdentityEntity> identityEntityOpt = identityCacheRepo.findById(idHash);
			if (identityEntityOpt.isPresent()) {
				identityEntity = identityEntityOpt.get();
				identityEntity.setUpdBy(IDA);
				identityEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
			} else {
				identityEntity = new IdentityEntity();
				identityEntity.setCrBy(IDA);
				identityEntity.setCrDTimes(DateUtils.getUTCCurrentDateTime());
				identityEntity.setId(idHash);
				identityEntity.setToken(token);
			}
			LocalDateTime expiryTimestamp = expiryTime == null ? null : DateUtils.parseUTCToLocalDateTime(expiryTime);
			identityEntity.setExpiryTimestamp(expiryTimestamp);
			identityEntity.setTransactionLimit(transactionLimit);

			identityEntity.setDemographicData(demoBytes);
			identityEntity.setBiometricData(bioBytes);
			return identityEntity;
		} catch (ClassCastException | JsonProcessingException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Store identity entity.
	 *
	 * @param idEntities the id entities
	 */
	@Override
	public void storeIdentityEntity(List<? extends IdentityEntity> idEntities) {
		identityCacheRepo.saveAll(idEntities);
	}

	/**
	 * Split demo bio data.
	 *
	 * @param credentialData the credential data
	 * @return the map[]
	 */
	private Map<String, Object>[] splitDemoBioData(Map<String, Object> credentialData) {
		Map<Boolean, List<Entry<String, Object>>> bioOrDemoData = credentialData.entrySet().stream()
				.collect(Collectors.partitioningBy(entry -> entry.getKey().toLowerCase().startsWith(BiometricType.FINGER.value().toLowerCase())
						|| entry.getKey().toLowerCase().startsWith(BiometricType.IRIS.value().toLowerCase())
						|| entry.getKey().toLowerCase().startsWith(BiometricType.FACE.value().toLowerCase())));
		
		Map<String, Object> demoData = bioOrDemoData.get(false).stream()
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		Map<String, Object> bioData = bioOrDemoData.get(true).stream()
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		return new Map[] { demoData, bioData };
	}

	/**
	 * Save salt.
	 *
	 * @param modulo the modulo
	 * @param salt   the salt
	 */
	private void saveSalt(String modulo, String salt) {
		Integer saltModulo = Integer.valueOf(modulo);
		if (!uinHashSaltRepo.existsById(saltModulo)) {
			IdaUinHashSalt saltEntity = new IdaUinHashSalt();
			saltEntity.setId(saltModulo);
			saltEntity.setSalt(salt);
			saltEntity.setCreatedBy(IDA);
			saltEntity.setCreatedDTimes(DateUtils.getUTCCurrentDateTime());
			uinHashSaltRepo.save(saltEntity);
		}
	}
	
	/**
	 * Process missing credential request id.
	 *
	 * @param dtos the dtos
	 */
	public void processMissingCredentialRequestId(List<? extends CredentialRequestIdsDto> dtos) {
		dtos.forEach(dto -> processMissingCredentialRequestId(dto));
	}
	
	/**
	 * Process missing credential request id.
	 *
	 * @param dto the dto
	 */
	private void processMissingCredentialRequestId(CredentialRequestIdsDto dto) {
		String requestId = dto.getRequestId();
		Optional<CredentialEventStore>  eventOpt = credentialEventRepo.findTop1ByCredentialTransactionIdOrderByCrDTimesDesc(requestId);
		if(eventOpt.isPresent()) {
			CredentialEventStore eventStore = eventOpt.get();
			String statusCode = eventStore.getStatusCode();
			mosipLogger.debug("Found existing credential with request-id {} and status {}..", requestId, statusCode);
			// For STORED, FAILED_WITH_MAX_RETRIES and FAILED_NON_RECOVERABLE, the status is
			// not yet updated for in credential request service, so notify that. 
			// STORED to be notified as STORED and FAILED_* as FAILED. 
			// For NEW and FAILED, events will be processed by credential store batch job, so noting to do.
			if(CredentialStoreStatus.STORED.name().equalsIgnoreCase(statusCode)) {
				mosipLogger.debug("Notifying credential with request-id {} as 'STORED'", requestId);
				credentialStoreStatusEventPublisher.publishEvent(CredentialStoreStatus.STORED.name(), requestId, eventStore.getCrDTimes());
			} else if(CredentialStoreStatus.FAILED_WITH_MAX_RETRIES.name().equalsIgnoreCase(statusCode)
					|| CredentialStoreStatus.FAILED_NON_RECOVERABLE.name().equalsIgnoreCase(statusCode)) {
				mosipLogger.debug("Notifying credential with request-id {} as 'FAILED'", requestId);
				credentialStoreStatusEventPublisher.publishEvent(CredentialStoreStatus.FAILED.name(), requestId, eventStore.getCrDTimes());
			}
		} else {
			//Re-trigger credential issuance
			retriggerCredentialIssuance(requestId);
		}
	}

	/**
	 * Retrigger credential issuance.
	 *
	 * @param requestId the request id
	 */
	private void retriggerCredentialIssuance(String requestId) {
		mosipLogger.info("Retriggering credential issuance with request-id {} ", requestId);
		try {
			credentialRequestManager.retriggerCredentialIssuance(requestId);
		} catch (RestServiceException | IDDataValidationException e) {
			// Throwing retry exception to perform job level retry
			throw new IdAuthRetryException(e);
		}
	}

}
