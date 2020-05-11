package io.mosip.authentication.common.service.impl.idevent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService;
import io.mosip.idrepository.core.constant.EventType;
import io.mosip.idrepository.core.dto.EventDTO;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * ID Change Event Handler service implementation class.
 *
 * @author Loganathan Sekar
 */
@Service
@Transactional
public class IdChengeEventHandlerServiceImpl implements IdChangeEventHandlerService {

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
	private static Logger mosipLogger = IdaLogger.getLogger(IdChengeEventHandlerServiceImpl.class);
	
	/** The id repo manager. */
	@Autowired
	private IdRepoManager idRepoManager;
	
	/** The id service. */
	@Autowired
	private IdService<?> idService;
	
	/** The identity cache repo. */
	@Autowired
	private IdentityCacheRepository identityCacheRepo;
	
	/** The key manager. */
	@Autowired
	private KeyManager keyManager;
	
	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService#handleIdEvent(java.util.List)
	 */
	@Override
	public boolean handleIdEvent(List<EventDTO> events) {
		Map<EventType, List<EventDTO>> eventsByType = events.stream()
				.collect(Collectors.groupingBy(EventDTO::getEventType));
		return Arrays.stream(EventType.values())
				.filter(eventsByType::containsKey)
				.allMatch(eventType -> 
						getFunctionForEventType(eventType)
							.apply(eventsByType.get(eventType)));

	}

	/**
	 * Gets the function for event type.
	 *
	 * @param eventType the event type
	 * @return the function for event type
	 */
	private Function<List<EventDTO>, Boolean> getFunctionForEventType(EventType eventType) {
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
			return list -> false;
		}
	}

	/**
	 * Handle create uin events.
	 *
	 * @param events the events
	 * @return true, if successful
	 */
	private boolean handleCreateUinEvents(List<EventDTO> events) {
		EnumSet<IdChangeProperties> properties = EnumSet.of(
				IdChangeProperties.UPDATE_ID_DATA, 
				IdChangeProperties.PREPARE_UIN_ENTITIES 
				);
		
		String logMethodName = "handleCreateUinEvents";
		return updateEntitiesForEvents(logMethodName, 
				events, 
				properties);
	}
	
	/**
	 * Handle update uin events.
	 *
	 * @param events the events
	 * @return true, if successful
	 */
	private boolean handleUpdateUinEvents(List<EventDTO> events) {
		EnumSet<IdChangeProperties> properties = EnumSet.of(
				IdChangeProperties.UPDATE_ID_DATA, 
				IdChangeProperties.PREPARE_UIN_ENTITIES, 
				IdChangeProperties.FIND_EXISTING_UIN_ENTITIES, 
				IdChangeProperties.UPDATE_EXISTING_UIN_ATTRIBUTES
				);
		
		String logMethodName = "handleUpdateUinEvents";
		return updateEntitiesForEvents(logMethodName, 
				events, 
				properties);
	}

	/**
	 * Handle create vid events.
	 *
	 * @param events the events
	 * @return true, if successful
	 */
	private boolean handleCreateVidEvents(List<EventDTO> events) {
		EnumSet<IdChangeProperties> properties = EnumSet.of(
				IdChangeProperties.UPDATE_ID_DATA, 
				IdChangeProperties.UPDATE_WITH_LOCAL_ID_DATA, 
				IdChangeProperties.PREPARE_VID_ENTITIES
				);
		
		String logMethodName = "handleCreateVidEvents";
		return updateEntitiesForEvents(logMethodName, 
				events, 
				properties);
	}

	/**
	 * Handle update vid events.
	 *
	 * @param events the events
	 * @return true, if successful
	 */
	private boolean handleUpdateVidEvents(List<EventDTO> events) {
		EnumSet<IdChangeProperties> properties = EnumSet.of(
				IdChangeProperties.PREPARE_VID_ENTITIES,
				IdChangeProperties.FIND_EXISTING_VID_ENTITIES,
				IdChangeProperties.UPDATE_EXISTING_VID_ATTRIBUTES
				);
		
		String logMethodName = "handleUpdateVidEvents";
		return updateEntitiesForEvents(logMethodName, 
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
	 */
	private boolean updateEntitiesForEvents(String logMethodName, 
			List<EventDTO> events, 
			EnumSet<IdChangeProperties> properties) {
		try {
			return updateEntitiesForEvents(events, properties);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					logMethodName, e.getMessage());
		}
		return false;
	}

	/**
	 * Update entities for events.
	 *
	 * @param events the events
	 * @param properties the properties
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private boolean updateEntitiesForEvents(List<EventDTO> events, 
			EnumSet<IdChangeProperties> properties) throws IdAuthenticationBusinessException {
		Map<String, List<EventDTO>> eventsByUin = events.stream().collect(Collectors.groupingBy(EventDTO::getUin));
		for(Entry<String, List<EventDTO>> entry : eventsByUin.entrySet()) {
			Optional<String> uinOpt = Optional.ofNullable(entry.getKey()); //UIN may be null
			List<EventDTO> uinEvents = entry.getValue();
			List<IdentityEntity> entities = prepareEntities(uinEvents, properties);
			
			//For VID update, if UIN is associated with the event, update VID entries with local UIN data
			if(properties.contains(IdChangeProperties.UPDATE_EXISTING_VID_ATTRIBUTES)) {
				if(uinOpt.isPresent()) {
					properties.add(IdChangeProperties.UPDATE_ID_DATA);
					properties.add(IdChangeProperties.UPDATE_WITH_LOCAL_ID_DATA);
				}
			}
			
			saveIdEntityByUinData(uinOpt, entities, properties);
		}
		return true;
	}

	/**
	 * Prepare entities.
	 *
	 * @param events the events
	 * @param properties the properties
	 * @return the list
	 */
	private List<IdentityEntity> prepareEntities(List<EventDTO> events, 
			EnumSet<IdChangeProperties> properties) {
		List<IdentityEntity> entities = new ArrayList<>();
		if(properties.contains(IdChangeProperties.PREPARE_UIN_ENTITIES)) {
			entities.addAll(
					prepareEntitiesById(
							EventDTO::getUin, 
							events, 
							properties.contains(IdChangeProperties.FIND_EXISTING_UIN_ENTITIES), 
							properties.contains(IdChangeProperties.UPDATE_EXISTING_UIN_ATTRIBUTES)));
		}
		if(properties.contains(IdChangeProperties.PREPARE_VID_ENTITIES)) {
			entities.addAll(
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
	private List<IdentityEntity> prepareEntitiesById(
			Function<EventDTO, String> idFunction,
			List<EventDTO> events, 
			boolean findExistingIdEntities,
			boolean updateExistingIdAttributes) {
		List<IdentityEntity> idEntities = new ArrayList<>();
		List<EventDTO> idEvents = getEventsByIdNonNull(idFunction, events);
		List<EventDTO> nonExistingIdEvents;
		if(findExistingIdEntities) {
			idEntities.addAll(prepareExistingEntitiesForEventsById(idEvents, idFunction, updateExistingIdAttributes));
			nonExistingIdEvents = findRemainingEventsExcludingEntities(idEvents, idEntities, idFunction);
		} else {
			nonExistingIdEvents = idEvents;
		}
		idEntities.addAll(createDistictEntitiesForEventsById(idFunction, nonExistingIdEvents));
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
	private List<EventDTO> findRemainingEventsExcludingEntities(List<EventDTO> events, List<IdentityEntity> entities,
			Function<EventDTO, String> idFunction) {
		return events.stream().filter(event -> {
				String id = idFunction.apply(event);
				String uinHash = idService.getUinHash(id);
				return entities.stream().noneMatch(entity -> entity.getId().equals(uinHash));
			}).collect(Collectors.toList());
	}

	/**
	 * Prepare existing entities for events by id.
	 *
	 * @param idEvents the id events
	 * @param idFunction the id function
	 * @param updateIdAttributes the update id attributes
	 * @return the list
	 */
	private List<IdentityEntity> prepareExistingEntitiesForEventsById(List<EventDTO> idEvents, 
			Function<EventDTO, String> idFunction, 
			boolean updateIdAttributes) {
		Map<String, EventDTO> eventById = idEvents.stream()
									.collect(Collectors.toMap(idFunction::apply, Function.identity()));
		Map<String, String> idsByIdHash = eventById.keySet()
								.stream()
								.filter(Objects::nonNull)
								.collect(Collectors.toMap(idService::getUinHash, Function.identity()));
		
		List<IdentityEntity> existingEntities = findEntitiesByIds(idsByIdHash.keySet().stream().collect(Collectors.toList()));
		if(updateIdAttributes) {
			existingEntities.forEach(entity -> {
				String idHash = entity.getId();
				String id = idsByIdHash.get(idHash);
				EventDTO eventDTO = eventById.get(id);
				entity.setExpiryTimestamp(eventDTO.getExpiryTimestamp());
				entity.setTransactionLimit(eventDTO.getTransactionLimit());
			});
		}
		return existingEntities;
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
	private List<IdentityEntity> createDistictEntitiesForEventsById(Function<EventDTO, String> idFunction, List<EventDTO> uinEvents) {
		return uinEvents.stream()
						.map(event -> mapEventToNewEntity(event, idFunction))
						.filter(Optional::isPresent)
						.map(Optional::get)
						.distinct()
						.collect(Collectors.toList());
	}
	
	/**
	 * Map event to new entity.
	 *
	 * @param event the event
	 * @param idFunction the id function
	 * @return the optional
	 */
	private Optional<IdentityEntity> mapEventToNewEntity(EventDTO event, Function<EventDTO, String> idFunction) {
		String id = idFunction.apply(event);
		if (id != null) {
			IdentityEntity identityEntity = new IdentityEntity();
			identityEntity.setId(idService.getUinHash(id));
			identityEntity.setExpiryTimestamp(event.getExpiryTimestamp());
			identityEntity.setTransactionLimit(event.getTransactionLimit());
			return Optional.of(identityEntity);
		}
		return Optional.empty();
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
			List<IdentityEntity> entities,
			EnumSet<IdChangeProperties> properties) throws IdAuthenticationBusinessException {
		Optional<byte[]> demoData;
		Optional<byte[]> bioData;
		if(properties.contains(IdChangeProperties.UPDATE_ID_DATA) && uinOpt.isPresent()) {
			String uin = uinOpt.get();
			if(properties.contains(IdChangeProperties.UPDATE_WITH_LOCAL_ID_DATA)) {
				Optional<IdentityEntity> entityOpt = identityCacheRepo.findById(idService.getUinHash(uin));
				if(entityOpt.isPresent()) {
					IdentityEntity entity = entityOpt.get();
					demoData = Optional.of(entity.getDemographicData());
					bioData = Optional.of(entity.getBiometricData());
				} else {
					demoData = Optional.empty();
					bioData = Optional.empty();
				}
			} else {
				Map<String, Object> identity = idRepoManager.getIdenity(uin, true);
				demoData = Optional.of(getDemoData(identity));
				bioData = Optional.of(getBioData(identity));
			}
		} else {
			demoData = Optional.empty();
			bioData = Optional.empty();
		}
		entities.forEach(entity -> saveIdEntity(entity, demoData, bioData));
	}

	/**
	 * Save id entity.
	 *
	 * @param entity the entity
	 * @param demoData the demo data
	 * @param bioData the bio data
	 */
	private void saveIdEntity(IdentityEntity entity,Optional<byte[]> demoData, Optional<byte[]> bioData) {
		String id = entity.getId();
		if(demoData.isPresent()) {
			entity.setDemographicData(keyManager.encrypt(id, demoData.get()));
		}
		if(bioData.isPresent()) {
			entity.setBiometricData(keyManager.encrypt(id, bioData.get()));
		}
		identityCacheRepo.save(entity);
	}

	/**
	 * Gets the demo data.
	 *
	 * @param identity the identity
	 * @return the demo data
	 */
	@SuppressWarnings("unchecked")
	private byte[] getDemoData(Map<String, Object> identity) {
		return Optional.ofNullable(identity.get("response"))
								.filter(obj -> obj instanceof Map)
								.map(obj -> ((Map<String, Object>)obj).get("identity"))
								.filter(obj -> obj instanceof Map)
								.map(obj -> {
									try {
										return mapper.writeValueAsBytes(obj);
									} catch (JsonProcessingException e) {
										mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
												"handleCreateUinEvent", e.getMessage());
									}
									return new byte[0];
								})
								.orElse(new byte[0]);
	}
	
	/**
	 * Gets the bio data.
	 *
	 * @param identity the identity
	 * @return the bio data
	 */
	@SuppressWarnings("unchecked")
	private byte[] getBioData(Map<String, Object> identity) {
		return Optional.ofNullable(identity.get("response"))
								.filter(obj -> obj instanceof Map)
								.map(obj -> ((Map<String, Object>)obj).get("documents"))
								.filter(obj -> obj instanceof List)
								.flatMap(obj -> 
										((List<Map<String, Object>>)obj)
											.stream()
											.filter(map -> map.containsKey("category") 
															&& map.get("category").toString().equalsIgnoreCase("individualBiometrics")
															&& map.containsKey("value"))
											.map(map -> (String)map.get("value"))
											.findAny())
								.map(CryptoUtil::decodeBase64)
								.orElse(new byte[0]);
	}

}
