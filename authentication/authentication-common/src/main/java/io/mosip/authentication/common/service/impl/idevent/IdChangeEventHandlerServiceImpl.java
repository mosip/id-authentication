package io.mosip.authentication.common.service.impl.idevent;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService;
import io.mosip.idrepository.core.constant.EventType;
import io.mosip.idrepository.core.dto.EventDTO;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * ID Change Event Handler service implementation class.
 *
 * @author Loganathan Sekar
 */
@Service
public class IdChangeEventHandlerServiceImpl implements IdChangeEventHandlerService {
	
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

	/**
	 * The Enum IdChangeProperties.
	 */
	private enum IdChangeProperties {
		
		/** The update id data. */
		UPDATE_ID_DATA, 
		
		/** The update with local id data. */
		UPDATE_WITH_LOCAL_ID_DATA, 
		
		/** The prepare uin entities. */
		PREPARE_UIN_ENTITIES, 
		
		/** The prepare vid entities. */
		PREPARE_VID_ENTITIES,
		
		/** The find existing uin entities. */
		FIND_EXISTING_UIN_ENTITIES, 
		
		/** The find existing vid entities. */
		FIND_EXISTING_VID_ENTITIES, 
		
		/** The update existing uin attributes. */
		UPDATE_EXISTING_UIN_ATTRIBUTES,
		
		/** The update existing vid attributes. */
		UPDATE_EXISTING_VID_ATTRIBUTES
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

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService#handleIdEvent(java.util.List)
	 */
	@Override
	public void handleIdEvent(List<EventDTO> events) throws IdAuthenticationBusinessException {
		try {
			doHandleEvents(events);
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
	private void doHandleEvents(List<EventDTO> events) throws IdAuthenticationBusinessException {
		Map<EventType, List<EventDTO>> eventsByType = events.stream()
				.collect(Collectors.groupingBy(EventDTO::getEventType));
		for (EventType eventType : EventType.values()) {
			if(eventsByType.containsKey(eventType)) {
				List<EventDTO> eventsList = eventsByType.get(eventType);
				try {
					getFunctionForEventType(eventType)
						.apply(eventsList);
					auditEvents(events, null);
				} catch (IdAuthenticationBusinessException e) {
					auditEvents(events, e);
					throw e;
				}
			}
		}
	}

	/**
	 * Audit events.
	 *
	 * @param events the events
	 * @param e the e
	 * @throws IDDataValidationException the ID data validation exception
	 */
	private void auditEvents(List<EventDTO> events, IdAuthenticationBusinessException e) throws IDDataValidationException {
		for (EventDTO eventDTO : events) {
			auditEvent(eventDTO, e);
		}
	}

	/**
	 * Audit event.
	 *
	 * @param event the event
	 * @param e the e
	 * @throws IDDataValidationException the ID data validation exception
	 */
	private void auditEvent(EventDTO event, IdAuthenticationBusinessException e) throws IDDataValidationException {
		if(e == null) {
			String message = event.getEventType() + " : Success";
			auditHelper.audit(AuditModules.IDENTITY_CACHE, getAuditEvent(event.getEventType()), getIdFunctionForEventType(event.getEventType()).apply(event), getIdTypeForEventType(event.getEventType()), message);
		} else {
			auditHelper.audit(AuditModules.IDENTITY_CACHE, getAuditEvent(event.getEventType()), getIdFunctionForEventType(event.getEventType()).apply(event), getIdTypeForEventType(event.getEventType()), e);
		}
	}

	/**
	 * Gets the id type for event type.
	 *
	 * @param eventType the event type
	 * @return the id type for event type
	 */
	private IdType getIdTypeForEventType(EventType eventType) {
		switch (eventType) {
		case CREATE_UIN:
		case UPDATE_UIN:
			return IdType.UIN;
		case CREATE_VID:
		case UPDATE_VID:
			return  IdType.VID;
		default:
			return IdType.DEFAULT_ID_TYPE;
		}
	}

	/**
	 * Gets the id function for event type.
	 *
	 * @param eventType the event type
	 * @return the id function for event type
	 */
	private Function<EventDTO, String> getIdFunctionForEventType(EventType eventType) {
		switch (eventType) {
		case CREATE_UIN:
		case UPDATE_UIN:
			return EventDTO::getUin;
		case CREATE_VID:
		case UPDATE_VID:
			return EventDTO::getVid;
		default:
			return EventDTO::getUin;
		}
	}

	/**
	 * Gets the audit event.
	 *
	 * @param eventType the event type
	 * @return the audit event
	 */
	private AuditEvents getAuditEvent(EventType eventType) {
		switch (eventType) {
		case CREATE_UIN:
			return AuditEvents.CREATE_UIN_EVENT;
		case UPDATE_UIN:
			return AuditEvents.UPDATE_UIN_EVENT;
		case CREATE_VID:
			return AuditEvents.CREATE_VID_EVENT;
		case UPDATE_VID:
			return AuditEvents.UPDATE_VID_EVENT;
		default:
			return AuditEvents.UPDATE_UIN_EVENT;
		}
	}

	/**
	 * Gets the function for event type.
	 *
	 * @param eventType the event type
	 * @return the function for event type
	 */
	private ConsumerWithBusinessException<List<EventDTO>, Boolean> getFunctionForEventType(EventType eventType) {
		switch (eventType) {
		case CREATE_UIN:
			return this::handleCreateUinEvents;
		case UPDATE_UIN:
			return this::handleUpdateUinEvents;
		case CREATE_VID:
			return this::handleCreateVidEvents;
		case UPDATE_VID:
			return this::handleUpdateVidEvents;
		default:
			return list -> {return;};
		}
	}

	/**
	 * Handle create uin events.
	 *
	 * @param events the events
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void handleCreateUinEvents(List<EventDTO> events) throws IdAuthenticationBusinessException {
		EnumSet<IdChangeProperties> properties = EnumSet.of(
				IdChangeProperties.UPDATE_ID_DATA, 
				IdChangeProperties.PREPARE_UIN_ENTITIES 
				);
		
		String logMethodName = "handleCreateUinEvents";
		updateEntitiesForEvents(logMethodName, 
				events, 
				properties);
	}
	
	/**
	 * Handle update uin events.
	 *
	 * @param events the events
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void handleUpdateUinEvents(List<EventDTO> events) throws IdAuthenticationBusinessException {

		EnumSet<IdChangeProperties> properties = EnumSet.of(
				IdChangeProperties.PREPARE_UIN_ENTITIES, 
				IdChangeProperties.FIND_EXISTING_UIN_ENTITIES, 
				IdChangeProperties.UPDATE_EXISTING_UIN_ATTRIBUTES
				);
		
		boolean deactivated = events.stream().anyMatch(event -> event.getExpiryTimestamp() != null
				&& event.getExpiryTimestamp().isBefore(getCurrentUTCLocalTime()));
		
		if(!deactivated) {
			properties.add(IdChangeProperties.UPDATE_ID_DATA);
		}
		
		String logMethodName = "handleUpdateUinEvents";
		updateEntitiesForEvents(logMethodName, 
				events, 
				properties);
	}

	/**
	 * Handle create vid events.
	 *
	 * @param events the events
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void handleCreateVidEvents(List<EventDTO> events) throws IdAuthenticationBusinessException {
		EnumSet<IdChangeProperties> properties = EnumSet.of(
				IdChangeProperties.UPDATE_ID_DATA, 
				IdChangeProperties.UPDATE_WITH_LOCAL_ID_DATA, 
				IdChangeProperties.PREPARE_VID_ENTITIES
				);
		
		String logMethodName = "handleCreateVidEvents";
		updateEntitiesForEvents(logMethodName, 
				events, 
				properties);
	}

	/**
	 * Handle update vid events.
	 *
	 * @param events the events
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void handleUpdateVidEvents(List<EventDTO> events) throws IdAuthenticationBusinessException {
		EnumSet<IdChangeProperties> properties = EnumSet.of(
				IdChangeProperties.PREPARE_VID_ENTITIES,
				IdChangeProperties.FIND_EXISTING_VID_ENTITIES,
				IdChangeProperties.UPDATE_EXISTING_VID_ATTRIBUTES
				);
		
		String logMethodName = "handleUpdateVidEvents";
		updateEntitiesForEvents(logMethodName, 
				events, 
				properties);
	}

	/**
	 * Update entities for events.
	 *
	 * @param logMethodName the log method name
	 * @param events the events
	 * @param properties the properties
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void updateEntitiesForEvents(String logMethodName, 
			List<EventDTO> events, 
			EnumSet<IdChangeProperties> properties) throws IdAuthenticationBusinessException {
		try {
			updateEntitiesForEvents(events, properties);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					logMethodName, e.getMessage());
			throw e;
		}
	}

	/**
	 * Update entities for events.
	 *
	 * @param events the events
	 * @param properties the properties
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void updateEntitiesForEvents(List<EventDTO> events, 
			EnumSet<IdChangeProperties> properties) throws IdAuthenticationBusinessException {
		Map<String, List<EventDTO>> eventsByUin = 
				events.stream()
					  .collect(
							Collectors.groupingBy(event ->
										Optional.ofNullable(event.getUin()).orElse("")));
		for(Entry<String, List<EventDTO>> entry : eventsByUin.entrySet()) {
			Optional<String> uinOpt = Optional.ofNullable(entry.getKey()); //UIN may be null
			List<EventDTO> uinEvents = entry.getValue();
			Map<String, IdentityEntity> entities = prepareEntities(uinEvents, properties);
			
			//For VID update, if UIN is associated with the event, update VID entries with local UIN data
			if(properties.contains(IdChangeProperties.UPDATE_EXISTING_VID_ATTRIBUTES)) {
				if(uinOpt.isPresent()) {
					properties.add(IdChangeProperties.UPDATE_ID_DATA);
					properties.add(IdChangeProperties.UPDATE_WITH_LOCAL_ID_DATA);
				}
			}
			
			saveIdEntityByUinData(uinOpt, entities, properties);
		}
	}

	/**
	 * Prepare entities.
	 *
	 * @param events the events
	 * @param properties the properties
	 * @return the list
	 */
	private Map<String, IdentityEntity> prepareEntities(List<EventDTO> events, 
			EnumSet<IdChangeProperties> properties) {
		Map<String, IdentityEntity> entities = new LinkedHashMap<>();
		if(properties.contains(IdChangeProperties.PREPARE_UIN_ENTITIES)) {
			entities.putAll(
					prepareEntitiesById(
							EventDTO::getUin, 
							events, 
							properties.contains(IdChangeProperties.FIND_EXISTING_UIN_ENTITIES), 
							properties.contains(IdChangeProperties.UPDATE_EXISTING_UIN_ATTRIBUTES)));
		}
		if(properties.contains(IdChangeProperties.PREPARE_VID_ENTITIES)) {
			entities.putAll(
					prepareEntitiesById(
							EventDTO::getVid, 
							events, 
							properties.contains(IdChangeProperties.FIND_EXISTING_VID_ENTITIES), 
							properties.contains(IdChangeProperties.UPDATE_EXISTING_VID_ATTRIBUTES)));
		}
		return entities;
	}

	/**
	 * Prepare entities by id.
	 *
	 * @param idFunction the id function
	 * @param events the events
	 * @param findExistingIdEntities the find existing id entities
	 * @param updateExistingIdAttributes the update existing id attributes
	 * @return the list
	 */
	private Map<String, IdentityEntity> prepareEntitiesById(
			Function<EventDTO, String> idFunction,
			List<EventDTO> events, 
			boolean findExistingIdEntities,
			boolean updateExistingIdAttributes) {
		Map<String, IdentityEntity> idEntities = new LinkedHashMap<>();
		List<EventDTO> idEvents = getEventsByIdNonNull(idFunction, events);
		List<EventDTO> nonExistingIdEvents;
		if(findExistingIdEntities) {
			idEntities.putAll(prepareExistingEntitiesForEventsById(idEvents, idFunction, updateExistingIdAttributes));
			nonExistingIdEvents = findRemainingEventsExcludingEntities(idEvents, idEntities.values(), idFunction);
		} else {
			nonExistingIdEvents = idEvents;
		}
		idEntities.putAll(createDistictEntitiesForEventsById(idFunction, nonExistingIdEvents));
		return idEntities;
	}

	/**
	 * Gets the events by id non null.
	 *
	 * @param idFunction the id function
	 * @param events the events
	 * @return the events by id non null
	 */
	private List<EventDTO> getEventsByIdNonNull(Function<EventDTO, String> idFunction, List<EventDTO> events) {
		return events.stream().filter(event -> idFunction.apply(event) != null).collect(Collectors.toList());
	}

	/**
	 * Find remaining events excluding entities.
	 *
	 * @param events the events
	 * @param entities the entities
	 * @param idFunction the id function
	 * @return the list
	 */
	private List<EventDTO> findRemainingEventsExcludingEntities(List<EventDTO> events, Collection<IdentityEntity> entities,
			Function<EventDTO, String> idFunction) {
		return events.stream().filter(event -> {
				String id = idFunction.apply(event);
				String uinHash = getHash(id);
				return entities.stream().noneMatch(entity -> entity.getId().equals(uinHash));
			}).collect(Collectors.toList());
	}

	/**
	 * Gets the hash.
	 *
	 * @param id the id
	 * @return the hash
	 */
	private String getHash(String id) {
		return securityManager.hash(id);
	}

	/**
	 * Prepare existing entities for events by id.
	 *
	 * @param idEvents the id events
	 * @param idFunction the id function
	 * @param updateIdAttributes the update id attributes
	 * @return the list
	 */
	private Map<String, IdentityEntity> prepareExistingEntitiesForEventsById(List<EventDTO> idEvents, 
			Function<EventDTO, String> idFunction, 
			boolean updateIdAttributes) {
		Map<String, EventDTO> eventById = idEvents.stream()
									.collect(Collectors.toMap(idFunction::apply, Function.identity()));
		Map<String, String> idsByIdHash = eventById.keySet()
								.stream()
								.filter(Objects::nonNull)
								.collect(Collectors.toMap(this::getHash, Function.identity()));
		
		List<IdentityEntity> existingEntities = findEntitiesByIds(idsByIdHash.keySet().stream().collect(Collectors.toList()));
		if(updateIdAttributes) {
			existingEntities.forEach(entity -> {
				String idHash = entity.getId();
				String id = idsByIdHash.get(idHash);
				EventDTO eventDTO = eventById.get(id);
				entity.setExpiryTimestamp(eventDTO.getExpiryTimestamp());
				entity.setTransactionLimit(eventDTO.getTransactionLimit());
				
				entity.setUpdBy("ida");
				entity.setUpdDTimes(getCurrentUTCLocalTime());
			});
		}
		return existingEntities.stream()
				.collect(Collectors.toMap(entity -> idsByIdHash.get(entity.getId()), Function.identity()));
	}

	/**
	 * Gets the current UTC local time.
	 *
	 * @return the current UTC local time
	 */
	private LocalDateTime getCurrentUTCLocalTime() {
		return DateUtils.getUTCCurrentDateTime();
	}
	
	/**
	 * Find entities by ids.
	 *
	 * @param ids the ids
	 * @return the list
	 */
	private List<IdentityEntity> findEntitiesByIds(List<String> ids) {
		return identityCacheRepo.findAllById(ids);
	}

	/**
	 * Creates the distict entities for events by id.
	 *
	 * @param idFunction the id function
	 * @param uinEvents the uin events
	 * @return the list
	 */
	private Map<String, IdentityEntity> createDistictEntitiesForEventsById(Function<EventDTO, String> idFunction, List<EventDTO> uinEvents) {
		return uinEvents.stream()
						.filter(event -> idFunction.apply(event) != null)
						.collect(Collectors.toMap(
								event -> idFunction.apply(event), 
								event -> mapEventToNewEntity(event, idFunction)));
	}
	
	/**
	 * Map event to new entity.
	 *
	 * @param event the event
	 * @param idFunction the id function
	 * @return the optional
	 */
	private IdentityEntity mapEventToNewEntity(EventDTO event, Function<EventDTO, String> idFunction) {
		String id = idFunction.apply(event);
		Objects.requireNonNull(id);
		IdentityEntity identityEntity = new IdentityEntity();
		identityEntity.setId(getHash(id));
		identityEntity.setExpiryTimestamp(event.getExpiryTimestamp());
		identityEntity.setTransactionLimit(event.getTransactionLimit());
		identityEntity.setCrBy("ida");
		identityEntity.setCrDTimes(getCurrentUTCLocalTime());
		return identityEntity;
	}

	/**
	 * Save id entity by uin data.
	 *
	 * @param uinOpt the uin opt
	 * @param entities the entities
	 * @param properties the properties
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void saveIdEntityByUinData(Optional<String> uinOpt, 
			Map<String, IdentityEntity> entities,
			EnumSet<IdChangeProperties> properties) throws IdAuthenticationBusinessException {
		Optional<byte[]> demoData;
		Optional<byte[]> bioData;
		if(properties.contains(IdChangeProperties.UPDATE_ID_DATA) && 
				uinOpt.filter(str -> !str.isEmpty()).isPresent()) {
			String uin = uinOpt.get();
			if(properties.contains(IdChangeProperties.UPDATE_WITH_LOCAL_ID_DATA)) {
				Optional<IdentityEntity> entityOpt = identityCacheRepo.findById(getHash(uin));
				if(entityOpt.isPresent()) {
					IdentityEntity entity = entityOpt.get();
					demoData = Optional.of(securityManager.decryptWithAES(uin, entity.getDemographicData()));
					bioData = Optional.of(securityManager.decryptWithAES(uin, entity.getBiometricData()));
				} else {
					demoData = Optional.empty();
					bioData = Optional.empty();
				}
			} else {
				Map<String, Object> identity = idRepoManager.getIdentity(uin, true);
				demoData = Optional.of(idService.getDemoData(identity));
				bioData = Optional.of(idService.getBioData(identity));
			}
		} else {
			demoData = Optional.empty();
			bioData = Optional.empty();
		}
		saveIdEntity(entities, demoData, bioData);
	}

	/**
	 * Save id entity.
	 *
	 * @param entities the entities
	 * @param demoData the demo data
	 * @param bioData the bio data
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void saveIdEntity(Map<String, IdentityEntity> entities,Optional<byte[]> demoData, Optional<byte[]> bioData) throws IdAuthenticationBusinessException {
		for(Entry<String, IdentityEntity> entry: entities.entrySet()){
			String id = entry.getKey();
			IdentityEntity entity = entry.getValue();
			if(demoData.isPresent()) {
				entity.setDemographicData(securityManager.encryptWithAES(id, demoData.get()));
			}
			if(bioData.isPresent()) {
				entity.setBiometricData(securityManager.encryptWithAES(id, bioData.get()));
			}
		}
		identityCacheRepo.saveAll(entities.values());
	}

}