package io.mosip.authentication.common.service.impl.idevent;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * ID Change Event Handler service implementation class
 * 
 * @author Loganathan Sekar
 *
 */
@Service
@Transactional
public class IdChengeEventHandlerServiceImpl implements IdChangeEventHandlerService {
	
	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdChengeEventHandlerServiceImpl.class);
	
	@Autowired
	private IdRepoManager idRepoManager;
	
	@Autowired
	private IdService<?> idService;
	
	@Autowired
	private IdentityCacheRepository identityCacheRepo;
	
	@Autowired
	private KeyManager keyManager;
	
	@Autowired
	private ObjectMapper mapper;

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

	private boolean handleCreateUinEvents(List<EventDTO> events) {
		boolean updateIdData = true;
		boolean prepareUinEntities = true;
		boolean prepareVidEntities = false;
		boolean findExistingUinEntities = false;
		boolean findExistingVidEntities = false;
		boolean updateExistingUinAttributes = false;
		boolean updateExistingVidAttributes = false;
		String logMethodName = "handleCreateUinEvents";
		return updateEntitiesForEvents(logMethodName, 
				events, 
				updateIdData, 
				prepareUinEntities, 
				prepareVidEntities, 
				findExistingUinEntities, 
				findExistingVidEntities,
				updateExistingUinAttributes,
				updateExistingVidAttributes);
	}
	
	private boolean handleUpdateUinEvents(List<EventDTO> events) {
		boolean updateIdData = true;
		boolean prepareUinEntities = true;
		boolean prepareVidEntities = true;
		boolean findExistingUinEntities = true;
		boolean findExistingVidEntities = true;
		boolean updateExistingUinAttributes = true;
		boolean updateExistingVidAttributes = false;
		String logMethodName = "handleUpdateUinEvents";
		return updateEntitiesForEvents(logMethodName, 
				events, 
				updateIdData, 
				prepareUinEntities, 
				prepareVidEntities, 
				findExistingUinEntities, 
				findExistingVidEntities,
				updateExistingUinAttributes,
				updateExistingVidAttributes);
	}

	private boolean handleCreateVidEvents(List<EventDTO> events) {
		boolean updateIdData = true;
		boolean prepareUinEntities = false;
		boolean prepareVidEntities = true;
		boolean findExistingUinEntities = false;
		boolean findExistingVidEntities = false;
		boolean updateExistingUinAttributes = false;
		boolean updateExistingVidAttributes = false;
		String logMethodName = "handleCreateVidEvents";
		return updateEntitiesForEvents(logMethodName, 
				events, 
				updateIdData, 
				prepareUinEntities, 
				prepareVidEntities, 
				findExistingUinEntities, 
				findExistingVidEntities,
				updateExistingUinAttributes,
				updateExistingVidAttributes);
	}

	private boolean handleUpdateVidEvents(List<EventDTO> events) {
		boolean updateIdData = false;
		boolean prepareUinEntities = false;
		boolean prepareVidEntities = true;
		boolean findExistingUinEntities = false;
		boolean findExistingVidEntities = true;
		boolean updateExistingUinAttributes = false;
		boolean updateExistingVidAttributes = true;
		String logMethodName = "handleUpdateVidEvents";
		return updateEntitiesForEvents(logMethodName, 
				events, 
				updateIdData, 
				prepareUinEntities, 
				prepareVidEntities, 
				findExistingUinEntities, 
				findExistingVidEntities,
				updateExistingUinAttributes,
				updateExistingVidAttributes);
	}

	private boolean updateEntitiesForEvents(String logMethodName, 
			List<EventDTO> events, 
			boolean updateIdData, 
			boolean prepareUinEntities, 
			boolean prepareVidEntities,
			boolean findExistingUinEntities, 
			boolean findExistingVidEntities, 
			boolean updateExistingUinAttributes,
			boolean updateExistingVidAttributes) {
		try {
			return updateEntitiesForEvents(events,
					updateIdData,  
					prepareUinEntities, 
					prepareVidEntities, 
					findExistingUinEntities,
					findExistingVidEntities, 
					updateExistingUinAttributes,
					updateExistingVidAttributes);
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					logMethodName, e.getMessage());
		}
		return false;
	}

	private boolean updateEntitiesForEvents(List<EventDTO> events, 
			boolean updateIdData, 
			boolean prepareUinEntities, 
			boolean prepareVidEntities,
			boolean findExistingUinEntities, 
			boolean findExistingVidEntities,
			boolean updateExistingUinAttributes,
			boolean updateExistingVidAttributes) throws IdAuthenticationBusinessException {
		Map<String, List<EventDTO>> eventsByUin = events.stream().collect(Collectors.groupingBy(EventDTO::getUin));
		for(Entry<String, List<EventDTO>> entry : eventsByUin.entrySet()) {
			String uin = entry.getKey(); //UIN may be null
			List<EventDTO> uinEvents = entry.getValue();
			List<IdentityEntity> entities = prepareEntities(uinEvents, 
					prepareUinEntities, 
					prepareVidEntities,
					findExistingUinEntities, 
					findExistingVidEntities, 
					updateExistingUinAttributes,
					updateExistingVidAttributes);
			saveIdEntityByUinData(updateIdData, Optional.ofNullable(uin), entities);
		}
		return true;
	}

	private List<IdentityEntity> prepareEntities(List<EventDTO> events, 
			boolean prepareUinEntities, 
			boolean prepareVidEntities,
			boolean findExistingUinEntities, 
			boolean findExistingVidEntities,
			boolean updateExistingUinAttributes,
			boolean updateExistingVidAttributes) {
		List<IdentityEntity> entities = new ArrayList<>();
		if(prepareUinEntities) {
			entities.addAll(prepareEntitiesByIdType(EventDTO::getUin, events, findExistingUinEntities, updateExistingUinAttributes));
		}
		if(prepareVidEntities) {
			entities.addAll(prepareEntitiesByIdType(EventDTO::getVid, events, findExistingVidEntities, updateExistingVidAttributes));
		}
		return entities;
	}

	private List<IdentityEntity> prepareEntitiesByIdType(
			Function<EventDTO, String> idTypeFun,
			List<EventDTO> events, 
			boolean findExistingIdEntities,
			boolean updateExistingIdAttributes) {
		List<IdentityEntity> idEntities = new ArrayList<>();
		List<EventDTO> idEvents = getEventsByIdTypeNonNull(idTypeFun, events);
		List<EventDTO> nonExistingIdEvents;
		if(findExistingIdEntities) {
			idEntities.addAll(prepareExistingEntitiesForEventsById(idEvents, idTypeFun, updateExistingIdAttributes));
			nonExistingIdEvents = findRemainingEventsExcludingEntities(idEvents, idEntities, idTypeFun);
		} else {
			nonExistingIdEvents = idEvents;
		}
		idEntities.addAll(createDistictEntitiesForEventsById(idTypeFun, nonExistingIdEvents));
		return idEntities;
	}

	private List<EventDTO> getEventsByIdTypeNonNull(Function<EventDTO, String> idTypeFun, List<EventDTO> events) {
		return events.stream().filter(event -> idTypeFun.apply(event) != null).collect(Collectors.toList());
	}

	private List<EventDTO> findRemainingEventsExcludingEntities(List<EventDTO> events, List<IdentityEntity> entities,
			Function<EventDTO, String> idFun) {
		return events.stream().filter(event -> {
				String id = idFun.apply(event);
				String uinHash = idService.getUinHash(id);
				return entities.stream().noneMatch(entity -> entity.getId().equals(uinHash));
			}).collect(Collectors.toList());
	}

	private List<IdentityEntity> prepareExistingEntitiesForEventsById(List<EventDTO> idEvents, 
			Function<EventDTO, String> idFun, 
			boolean updateIdAttributes) {
		Map<String, EventDTO> eventById = idEvents.stream()
									.collect(Collectors.toMap(event -> idFun.apply(event), Function.identity()));
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
	
	private List<IdentityEntity> findEntitiesByIds(List<String> ids) {
		return identityCacheRepo.findAllById(ids);
	}

	private List<IdentityEntity> createDistictEntitiesForEventsById(Function<EventDTO, String> idFun, List<EventDTO> uinEvents) {
		return uinEvents.stream()
						.map(event -> mapEventToNewEntity(event, idFun))
						.filter(Optional::isPresent)
						.map(Optional::get)
						.distinct()
						.collect(Collectors.toList());
	}
	
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

	private void saveIdEntityByUinData(boolean updateIdData, Optional<String> uinOpt, List<IdentityEntity> entities) throws IdAuthenticationBusinessException {
		Optional<byte[]> demoData;
		Optional<byte[]> bioData;
		if(updateIdData && uinOpt.isPresent()) {
			Map<String, Object> identity = idRepoManager.getIdenity(uinOpt.get(), true);
			demoData = Optional.of(getDemoData(identity));
			bioData = Optional.of(getBioData(identity));
		} else {
			demoData = Optional.empty();
			bioData = Optional.empty();
		}
		entities.forEach(entity -> saveIdEntity(entity, demoData, bioData));
	}

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
								.map(str -> {
									try {
										return str.getBytes("utf-8");
									} catch (UnsupportedEncodingException e) {
										mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
												"handleCreateUinEvent", e.getMessage());
									}
									return new byte[0];
								})
								.orElse(new byte[0]);
	}

}
