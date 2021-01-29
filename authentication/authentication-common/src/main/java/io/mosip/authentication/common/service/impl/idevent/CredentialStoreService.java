package io.mosip.authentication.common.service.impl.idevent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.entity.UinHashSalt;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.integration.dto.DataShareManager;
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
import io.mosip.idrepository.core.dto.Event;
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

@Component
public class CredentialStoreService {
	

	private static final String BIO_KEY = "bioEncryptedRandomKey";

	private static final String BIO_KEY_INDEX = "bioRankomKeyIndex";

	private static final String DEMO_KEY = "demoEncryptedRandomKey";

	private static final String DEMO_KEY_INDEX = "demoRankomKeyIndex";
	
	private static final String TOKEN = "TOKEN";

	private static final String IDA = "IDA";

	private static final String EXPIRY_TIME = "expiry_timestamp";

	private static final String TRANSACTION_LIMIT = "transaction_limit";

	private static final String SALT = "SALT";

	private static final String MODULO = "MODULO";

	private static final String ID_HASH = "id_hash";
	
	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(CredentialStoreService.class);
	
	/** The identity cache repo. */
	@Autowired
	private IdentityCacheRepository identityCacheRepo;
	
	/** The audit helper. */
	@Autowired
	private AuditHelper auditHelper;
	
	@Autowired
	private DataShareManager dataShareManager;
	
	@Autowired
	private UinHashSaltRepo uinHashSaltRepo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	/** The security manager. */
	@Autowired
	private IdAuthSecurityManager securityManager;

	@Value("${ida.credential.store.max.retry.limit:20}")
	private int maxRetryCount;
	
	@Value("${ida.credential.store.max.interval.millisecs:60000}")
	private int retryInterval;
	
	@Autowired
	private CredentialEventStoreRepository credentialEventRepo;
	
	public IdentityEntity processCredentialStoreEvent(CredentialEventStore credentialEventStore) throws IdAuthenticationBusinessException, RetryingBeforeRetryIntervalException {
		boolean alreadyFailed = credentialEventStore.getStatusCode().equals(CredentialStoreStatus.FAILED.name());
		
		if(alreadyFailed) {
			skipIfWaitingForRetryInterval(credentialEventStore.getUpdDTimes());
		}
		
		try {
			IdentityEntity entity = doProcessCredentialStoreEvent(credentialEventStore);
			updateEventProcessingStatus(credentialEventStore, true, alreadyFailed);		
			return entity;
		} catch (Exception e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					"processCredentialStoreEvent", "Error in Processing credential store event: " + e.getMessage());
			updateEventProcessingStatus(credentialEventStore, false, alreadyFailed);		
			throw e;
		}
	}
	
	private void skipIfWaitingForRetryInterval(LocalDateTime updDTimes) throws RetryingBeforeRetryIntervalException {
		if(DateUtils.getUTCCurrentDateTime().isBefore(updDTimes.plus(retryInterval, ChronoUnit.MILLIS))) {
			throw new RetryingBeforeRetryIntervalException();
		}
	}

	private void updateEventProcessingStatus(CredentialEventStore credentialEventStore, boolean isSuccess, boolean alreadyFailed) {
		credentialEventStore.setUpdBy(IDA);
		credentialEventStore.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
		
		if(isSuccess) {
			credentialEventStore.setStatusCode(CredentialStoreStatus.STORED.name());
		} else {
			int retryCount = 0;
			if(alreadyFailed) {
				retryCount = credentialEventStore.getRetryCount() + 1;
				credentialEventStore.setRetryCount(retryCount);
			}
			if(retryCount < maxRetryCount) {
				credentialEventStore.setStatusCode(CredentialStoreStatus.FAILED.name());
			} else {
				credentialEventStore.setStatusCode(CredentialStoreStatus.FAILED_WITH_MAX_RETRIES.name());
			}
		}
		
		credentialEventRepo.save(credentialEventStore);
	}
	
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
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					"storeEventModel", "error in json processing: " + e.getMessage());
		}
	}

	public IdentityEntity doProcessCredentialStoreEvent(CredentialEventStore credentialEventStore) throws IdAuthenticationBusinessException {
		String eventObjectStr = credentialEventStore.getEventObject();
		try {
			EventModel eventModel = objectMapper.readValue(eventObjectStr.getBytes(), EventModel.class);
			Event event = eventModel.getEvent();
			
			mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					"processCredentialStoreEvent", "Processing credential store event: " + eventObjectStr);
			
			String dataShareUri = event.getDataShareUri();
			if(dataShareUri != null) {
				try {
					Map<String, Object> additionalData = event.getData();
					String modulo = (String) additionalData.get(MODULO);
					String salt = (String) additionalData.get(SALT);
					String demoKeyIndex = (String) additionalData.get(DEMO_KEY_INDEX);
					String demoKey = (String) additionalData.get(DEMO_KEY);
					String bioKeyIndex = (String) additionalData.get(BIO_KEY_INDEX);
					String bioKey = (String) additionalData.get(BIO_KEY);
					
					saveSalt(modulo, salt);
					
					if(demoKeyIndex != null && demoKey != null) {
						securityManager.reEncryptAndStoreRandomKey(demoKeyIndex, demoKey);
					}
					
					if(bioKeyIndex != null && bioKey != null) {
						securityManager.reEncryptAndStoreRandomKey(bioKeyIndex, bioKey);
					}
					
					String idHash = (String) additionalData.get(ID_HASH);
					Integer transactionLimit = (Integer) additionalData.get(TRANSACTION_LIMIT);
					String expiryTime = (String) additionalData.get(EXPIRY_TIME);
					String token  = (String) additionalData.get(TOKEN);
					Map<String, Object> credentialData = dataShareManager.downloadObject(dataShareUri, Map.class);
					return createIdentityEntity(idHash, token, transactionLimit, expiryTime, credentialData);
					
				} catch (RestServiceException | IDDataValidationException e) {
					throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,e);
				}
			} else {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
						IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage()
								+ ": Data Share URI is not proivded in the event");
			}
		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					"retrieveAndStoreCredential", "Error parsing event model: " + eventObjectStr);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage()
							+ ": Error parsing event model: ");
		}
	}

	@SuppressWarnings("unchecked")
	private IdentityEntity createIdentityEntity(String idHash, String token, Integer transactionLimit, String expiryTime,
			Map<String, Object> credentialData) throws IdAuthenticationBusinessException {
		Map<String, Object>[] demoBioData = splitDemoBioData(
				(Map<String, Object>) credentialData.get(IdAuthCommonConstants.CREDENTIAL_SUBJECT));
		try {
			byte [] demoBytes = objectMapper.writeValueAsBytes(demoBioData[0]);
			byte [] bioBytes = objectMapper.writeValueAsBytes(demoBioData[1]);
			
			IdentityEntity identityEntity = new IdentityEntity();
			Optional<IdentityEntity> identityEntityOpt = identityCacheRepo.findById(idHash);
			if(identityEntityOpt.isPresent()) {
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
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,e);
		}
	}
	
	public void storeIdentityEntity(List<? extends IdentityEntity> idEntities) {
		identityCacheRepo.saveAll(idEntities);
	}

	private Map<String, Object>[] splitDemoBioData(Map<String, Object> credentialData) {
		Map<Boolean, List<Entry<String, Object>>> bioOrDemoData = credentialData.entrySet().stream().collect(Collectors
				.partitioningBy(entry -> entry.getKey().equalsIgnoreCase(IdAuthCommonConstants.INDIVIDUAL_BIOMETRICS)));
		
		Map<String, Object> demoData = bioOrDemoData.get(false).stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		Map<String, Object> bioData = bioOrDemoData.get(true).stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		return new Map[] {demoData, bioData};
	}

	private void saveSalt(String modulo, String salt) {
		Long saltModulo = Long.valueOf(modulo);
		if(!uinHashSaltRepo.existsById(saltModulo)) {
			UinHashSalt saltEntity = new UinHashSalt();
			saltEntity.setId(saltModulo);
			saltEntity.setSalt(salt);
			saltEntity.setCreatedBy(IDA);
			saltEntity.setCreatedDTimes(DateUtils.getUTCCurrentDateTime());
			uinHashSaltRepo.save(saltEntity);
		}
	}

}
