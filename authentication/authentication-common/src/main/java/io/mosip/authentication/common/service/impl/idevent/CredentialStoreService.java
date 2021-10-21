package io.mosip.authentication.common.service.impl.idevent;

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
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.entity.UinHashSalt;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.integration.DataShareManager;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.exception.RetryingBeforeRetryIntervalException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;

/**
 * The CredentialStoreService - the service to store credentials in IDA DB from
 * the credential issuance event.
 *
 * @author Loganathan Sekar
 */
@Component
public class CredentialStoreService {

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

	/** The Constant IDA. */
	private static final String IDA = "IDA";

	/** The Constant EXPIRY_TIME. */
	private static final String EXPIRY_TIME = "expiry_timestamp";

	/** The Constant TRANSACTION_LIMIT. */
	private static final String TRANSACTION_LIMIT = "transaction_limit";

	/** The Constant SALT. */
	private static final String SALT = "SALT";

	/** The Constant MODULO. */
	private static final String MODULO = "MODULO";

	/** The Constant ID_HASH. */
	private static final String ID_HASH = "id_hash";

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(CredentialStoreService.class);

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
	private UinHashSaltRepo uinHashSaltRepo;

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
	public IdentityEntity processCredentialStoreEvent(CredentialEventStore credentialEventStore)
			throws IdAuthenticationBusinessException, RetryingBeforeRetryIntervalException {
		boolean alreadyFailed = credentialEventStore.getStatusCode().equals(CredentialStoreStatus.FAILED.name());

		if (alreadyFailed) {
			skipIfWaitingForRetryInterval(credentialEventStore);
		}

		try {
			IdentityEntity entity = doProcessCredentialStoreEvent(credentialEventStore);
			updateEventProcessingStatus(credentialEventStore, true, false, alreadyFailed);
			//TODO Add audit log for credential store success
			return entity;
		} catch (RuntimeException e) {
			// Any Runtime exception is marked as non-recoverable and hence retry is skipped for that
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					"processCredentialStoreEvent", "Error in Processing credential store event: " + e.getMessage());
			updateEventProcessingStatus(credentialEventStore, false, false, alreadyFailed);
			//TODO Add audit log for credential store success
			throw e;
		} catch (Exception e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					"processCredentialStoreEvent", "Error in Processing credential store event: " + e.getMessage());
			updateEventProcessingStatus(credentialEventStore, false, true, alreadyFailed);
			//TODO Add audit log for credential store success
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
	 * @param alreadyFailed        the already failed
	 */
	@Transactional
	private void updateEventProcessingStatus(CredentialEventStore credentialEventStore, boolean isSuccess, boolean isRecoverableException,
			boolean alreadyFailed) {
		credentialEventStore.setUpdBy(IDA);
		credentialEventStore.setUpdDTimes(DateUtils.getUTCCurrentDateTime());

		if (isSuccess) {
			credentialEventStore.setStatusCode(CredentialStoreStatus.STORED.name());
			// TODO send websub event success message for the event id
		} else {
			if (isRecoverableException) {
				int retryCount = 0;
				if (alreadyFailed) {
					retryCount = credentialEventStore.getRetryCount() + 1;
					credentialEventStore.setRetryCount(retryCount);
				}
				if (retryCount < maxRetryCount) {
					credentialEventStore.setStatusCode(CredentialStoreStatus.FAILED.name());
				} else {
					credentialEventStore.setStatusCode(CredentialStoreStatus.FAILED_WITH_MAX_RETRIES.name());
					// TODO send websub event failure message for the event id
				}
			} else {
				// Any Runtime exception is marked as non-recoverable and hence retry is skipped for that
				credentialEventStore.setStatusCode(CredentialStoreStatus.FAILED_NON_RECOVERABLE.name());
				// TODO send websub event failure message for the event id
			}
		}

		credentialEventRepo.save(credentialEventStore);
	}

	/**
	 * Store event model.
	 *
	 * @param eventModel the event model
	 */
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
	public IdentityEntity doProcessCredentialStoreEvent(CredentialEventStore credentialEventStore)
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
					Map<String, Object> credentialData = dataShareManager.downloadObject(dataShareUri, Map.class);
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
		Map<Boolean, List<Entry<String, Object>>> bioOrDemoData = credentialData.entrySet().stream().collect(Collectors
				.partitioningBy(entry -> entry.getKey().equalsIgnoreCase(IdAuthCommonConstants.INDIVIDUAL_BIOMETRICS)));

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
		Long saltModulo = Long.valueOf(modulo);
		if (!uinHashSaltRepo.existsById(saltModulo)) {
			UinHashSalt saltEntity = new UinHashSalt();
			saltEntity.setId(saltModulo);
			saltEntity.setSalt(salt);
			saltEntity.setCreatedBy(IDA);
			saltEntity.setCreatedDTimes(DateUtils.getUTCCurrentDateTime());
			uinHashSaltRepo.save(saltEntity);
		}
	}

}
