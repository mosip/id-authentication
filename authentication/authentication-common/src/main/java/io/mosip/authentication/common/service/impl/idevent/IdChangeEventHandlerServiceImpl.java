package io.mosip.authentication.common.service.impl.idevent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.entity.UinHashSalt;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.integration.dto.DataShareManager;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.idevent.service.CredentialStoreService;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.idrepository.core.dto.Event;
import io.mosip.idrepository.core.dto.EventModel;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * ID Change Event Handler service implementation class.
 *
 * @author Loganathan Sekar
 */
@Service
public class IdChangeEventHandlerServiceImpl implements CredentialStoreService {
	
	private static final String TOKEN = "TOKEN";

	private static final String IDA = "IDA";

	private static final String EXPIRY_TIME = "expiry_time";

	private static final String TRANSACTION_LIMIT = "transaction_limit";

	private static final String BIO_KEY = "bioEncryptedRandomKey";

	private static final String BIO_KEY_INDEX = "bioRankomKeyIndex";

	private static final String DEMO_KEY = "demoEncryptedRandomKey";

	private static final String DEMO_KEY_INDEX = "demoRankomKeyIndex";

	private static final String SALT = "SALT";

	private static final String MODULO = "MODULO";

	private static final String ID_HASH = "ID_HASH";

	/**
	 * The Interface ConsumerWithBusinessException.
	 *
	 * @param <T> the generic type
	 * @param <R> the generic type
	 */
	@FunctionalInterface
	static interface ConsumerWithBusinessException<T,R> {
		
		/**
		 * Apply.
		 *
		 * @param t the t
		 * @throws IdAuthenticationBusinessException the id authentication business exception
		 */
		void apply(T t) throws IdAuthenticationBusinessException;
	}

	
	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdChangeEventHandlerServiceImpl.class);
	
	/** The id repo manager. */
	@Autowired
	private IdRepoManager idRepoManager;
	
	/** The security manager. */
	@Autowired
	private IdAuthSecurityManager securityManager;
	
	/** The identity cache repo. */
	@Autowired
	private IdentityCacheRepository identityCacheRepo;
	
	/** The mapper. */
	@Autowired
	private IdService<?> idService;
	
	/** The audit helper. */
	@Autowired
	private AuditHelper auditHelper;
	
	private DataShareManager dataShareManager;
	
	private UinHashSaltRepo uinHashSaltRepo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.idevent.service.CredentialStoreService#handleIdEvent(java.util.List)
	 */
	@Override
	public void handleIdEvent(EventModel event) throws IdAuthenticationBusinessException {
		try {
			doHandleEvent(event);
		} catch (IdAuthenticationBusinessException e) {
			throw e;
		} catch (Exception e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					"handleIdEvent", e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Do handle events.
	 *
	 * @param events the events
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Transactional
	private void doHandleEvent(EventModel eventModel) throws IdAuthenticationBusinessException {
		try {
			getFunctionForEventType(eventModel.getTopic())
				.apply(eventModel);
			auditEvent(eventModel, null);
		} catch (IdAuthenticationBusinessException e) {
			auditEvent(eventModel, e);
			throw e;
		}
	}


	/**
	 * Audit event.
	 *
	 * @param event the event
	 * @param e the e
	 * @throws IDDataValidationException the ID data validation exception
	 */
	private void auditEvent(EventModel event, IdAuthenticationBusinessException e) throws IDDataValidationException {
		String topic = event.getTopic();
		if(e == null) {
			String message = topic + " : Success";
			auditHelper.audit(AuditModules.IDENTITY_CACHE, getAuditEvent(topic), "id", "idType", message);
		} else {
			auditHelper.audit(AuditModules.IDENTITY_CACHE, getAuditEvent(topic), "id", "idType", e);
		}
	}


	/**
	 * Gets the audit event.
	 *
	 * @param eventType the event type
	 * @return the audit event
	 */
	private AuditEvents getAuditEvent(String eventTopic) {
		if (IDAEventType.CREDENTIAL_ISSUED.toString().equals(eventTopic)) {
			return AuditEvents.CREDENTIAL_ISSUED_EVENT;
		} else if (IDAEventType.REMOVE_ID.toString().equals(eventTopic)) {
			return AuditEvents.REMOVE_ID_EVENT;
		} else if (IDAEventType.DEACTIVATE_ID.toString().equals(eventTopic)) {
			return AuditEvents.DEACTIVATE_ID_EVENT;
		} else if (IDAEventType.ACTIVATE_ID.toString().equals(eventTopic)) {
			return AuditEvents.ACTIVATE_ID_EVENT;
		} else {
			return AuditEvents.ACTIVATE_ID_EVENT;
		}
	}

	/**
	 * Gets the function for event type.
	 *
	 * @param eventType the event type
	 * @return the function for event type
	 */
	private ConsumerWithBusinessException<EventModel, Void> getFunctionForEventType(String eventTopic) {
		
		if (IDAEventType.CREDENTIAL_ISSUED.toString().equals(eventTopic)) {
			return this::handleCredentialIssued;
		} else if (IDAEventType.REMOVE_ID.toString().equals(eventTopic)) {
			return this::handleRemoveId;
		} else if (IDAEventType.DEACTIVATE_ID.toString().equals(eventTopic)) {
			return this::handleDeactivateId;
		} else if (IDAEventType.ACTIVATE_ID.toString().equals(eventTopic)) {
			return this::handleActicateId;
		} else {
			return list -> {return;};
		}
	}

	private void handleCredentialIssued(EventModel eventModel) throws IdAuthenticationBusinessException {
		Event event = eventModel.getEvent();
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
				
				securityManager.reEncryptAndStoreRandomKey(demoKeyIndex, demoKey);
				securityManager.reEncryptAndStoreRandomKey(bioKeyIndex, bioKey);
				
				String idHash = (String) additionalData.get(ID_HASH);
				String transactionLimit = (String) additionalData.get(TRANSACTION_LIMIT);
				String expiryTime = (String) additionalData.get(EXPIRY_TIME);
				String token  = (String) additionalData.get(TOKEN);
				Map<String, Object> credentialData = dataShareManager.downloadObject(dataShareUri, Map.class);
				storeIdentityEntity(idHash, token, transactionLimit, expiryTime, credentialData);
				
			} catch (RestServiceException | IDDataValidationException e) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,e);
			}
		}
	}

	private void storeIdentityEntity(String idHash, String token, String transactionLimit, String expiryTime,
			Map<String, Object> credentialData) throws IdAuthenticationBusinessException {
		Map<String, Object>[] demoBioData =  splitDemoBioData(credentialData);
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
			Integer transactionLimitInt = transactionLimit == null ? null : Integer.parseInt(transactionLimit);
			identityEntity.setTransactionLimit(transactionLimitInt);
			
			identityEntity.setDemographicData(demoBytes);
			identityEntity.setBiometricData(bioBytes);
		} catch (JsonProcessingException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS,e);
		}
	}

	private Map<String, Object>[] splitDemoBioData(Map<String, Object> credentialData) {
		Map<Boolean, List<Entry<String, Object>>> bioOrDemoData = credentialData.entrySet().stream().collect(Collectors.partitioningBy(entry -> {
			String key = entry.getKey();
			return key.equalsIgnoreCase(SingleType.FACE.name()) ||
					key.equalsIgnoreCase(SingleType.IRIS.name()) ||
					key.equalsIgnoreCase(SingleType.FINGER.name());
		}));
		
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
	
	private void handleRemoveId(EventModel eventModel) throws IdAuthenticationBusinessException {
		Event event = eventModel.getEvent();
		Map<String, Object> additionalData = event.getData();
		String idHash = (String) additionalData.get(ID_HASH);
		Optional<IdentityEntity> identityEntityOpt = identityCacheRepo.findById(idHash);
		if(identityEntityOpt.isPresent()) {
			identityCacheRepo.delete(identityEntityOpt.get());
		}
	}
	
	private void handleDeactivateId(EventModel eventModel) throws IdAuthenticationBusinessException {
		updateIdentityMetadata(eventModel);
	}

	private void updateIdentityMetadata(EventModel eventModel) {
		Event event = eventModel.getEvent();
		Map<String, Object> additionalData = event.getData();
		String idHash = (String) additionalData.get(ID_HASH);
		Optional<IdentityEntity> identityEntityOpt = identityCacheRepo.findById(idHash);
		
		String transactionLimit = (String) additionalData.get(TRANSACTION_LIMIT);
		String expiryTime = (String) additionalData.get(EXPIRY_TIME);
		
		if(identityEntityOpt.isPresent()) {
			IdentityEntity identityEntity = identityEntityOpt.get();
			identityEntity.setUpdBy(IDA);
			identityEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
			
			LocalDateTime expiryTimestamp = expiryTime == null ? null : DateUtils.parseUTCToLocalDateTime(expiryTime);
			identityEntity.setExpiryTimestamp(expiryTimestamp);
			Integer transactionLimitInt = transactionLimit == null ? null : Integer.parseInt(transactionLimit);
			identityEntity.setTransactionLimit(transactionLimitInt);
			
			identityCacheRepo.save(identityEntity);
		}
	}
	
	private void handleActicateId(EventModel eventModel) throws IdAuthenticationBusinessException {
		updateIdentityMetadata(eventModel);
	}

}