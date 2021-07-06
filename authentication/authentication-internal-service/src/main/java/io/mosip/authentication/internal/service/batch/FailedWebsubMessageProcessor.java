package io.mosip.authentication.internal.service.batch;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.IDA;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_AUTH_PARTNER_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_CA_CERT_TOPIC;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HOTLIST_TOPIC;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TEMPLATES_TOPIC;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_MASTERDATA_TITLES_TOPIC;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.FailedMessageEntity;
import io.mosip.authentication.common.service.helper.WebSubHelper.FailedMessage;
import io.mosip.authentication.common.service.impl.idevent.CredentialStoreStatus;
import io.mosip.authentication.common.service.impl.idevent.MessageStoreStatus;
import io.mosip.authentication.common.service.impl.patrner.PartnerCACertEventService;
import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.common.service.repository.FailedMessagesRepo;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.PartnerEventTypes;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtype.status.service.UpdateAuthtypeStatusService;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService;
import io.mosip.authentication.core.spi.masterdata.MasterDataCacheUpdateService;
import io.mosip.idrepository.core.constant.IDAEventType;
import io.mosip.idrepository.core.dto.AuthTypeStatusEventDTO;
import io.mosip.kernel.core.function.ConsumerWithThrowable;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;

/**
 * The Class FailedWebsubMessageProcessor.
 * 
 * @author Loganathan Sekar
 */
@Component
public class FailedWebsubMessageProcessor {

	/** The mosip logger. */
	private static final Logger mosipLogger = IdaLogger.getLogger(FailedWebsubMessageProcessor.class);

	/** The Constant DUMMY_CONSUMER. */
	private static final ConsumerWithThrowable<FailedMessageEntity, Exception> DUMMY_CONSUMER = t -> {
		return;
	};

	/** The auth parther id. */
	@Value("${" + IDA_AUTH_PARTNER_ID + "}")
	private String authPartherId;

	/** The credential store service. */
	@Autowired
	private IdChangeEventHandlerService credentialStoreService;

	/** The authtype status service. */
	@Autowired
	private UpdateAuthtypeStatusService authtypeStatusService;

	/** The hotlist service. */
	@Autowired
	private HotlistService hotlistService;

	/** The master data cache update service. */
	@Autowired
	private MasterDataCacheUpdateService masterDataCacheUpdateService;

	/** The partner CA cert event service. */
	@Autowired
	private PartnerCACertEventService partnerCACertEventService;

	/** The partner manager. */
	@Autowired
	private PartnerServiceManager partnerManager;

	/** The hotlist event topic. */
	@Value("${" + IDA_WEBSUB_HOTLIST_TOPIC + "}")
	private String hotlistEventTopic;

	/** The masterdata templates event topic. */
	@Value("${" + IDA_WEBSUB_MASTERDATA_TEMPLATES_TOPIC + "}")
	private String masterdataTemplatesEventTopic;

	/** The masterdata titles event topic. */
	@Value("${" + IDA_WEBSUB_MASTERDATA_TITLES_TOPIC + "}")
	private String masterdataTitlesEventTopic;

	/** The partner cert event topic. */
	@Value("${" + IDA_WEBSUB_CA_CERT_TOPIC + "}")
	private String partnerCertEventTopic;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The env. */
	@Autowired
	protected Environment env;

	/** The failed messages repo. */
	@Autowired
	private FailedMessagesRepo failedMessagesRepo;

	/**
	 * Process failed websub messages.
	 *
	 * @param failedMessages the failed messages
	 */
	public void storeFailedWebsubMessages(List<? extends FailedMessage> failedMessages) {
		failedMessages.forEach(this::storeFailedMessage);
	}

	/**
	 * Process failed websub messages.
	 *
	 * @param failedMessageEntities the failed message entities
	 */
	public void processFailedWebsubMessages(List<? extends FailedMessageEntity> failedMessageEntities) {
		failedMessageEntities.forEach(this::processFailedMessage);
	}

	/**
	 * Store failed message.
	 *
	 * @param failedMessage the failed message
	 */
	private void storeFailedMessage(FailedMessage failedMessage) {
		Optional<EventModel> eventModelOpt = getEventModel(failedMessage);
		if (eventModelOpt.isPresent()) {
			EventModel eventModel = eventModelOpt.get();
			LocalDateTime failedDTimes = getFailedDTimes(failedMessage);
			Optional<String> id = Optional.ofNullable(eventModel.getEvent()).map(Event::getId);
			if (id.isPresent()) {
				storeFailedMessage(eventModel, id.get(), failedDTimes);
			}
		}
	}

	/**
	 * Gets the failed D times.
	 *
	 * @param failedMessage the failed message
	 * @return the failed D times
	 */
	private LocalDateTime getFailedDTimes(FailedMessage failedMessage) {
		// Get last message and assign it as current effectiveDtimes
		String timestampStr = failedMessage.getTimestamp();
		return DateUtils.parseUTCToLocalDateTime(timestampStr,
				env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
	}

	/**
	 * Store failed message.
	 *
	 * @param eventModel the event model
	 * @param id the id
	 * @param failedDtimes the failed dtimes
	 */
	public void storeFailedMessage(EventModel eventModel, String id, LocalDateTime failedDtimes) {
		if(failedMessagesRepo.findById(id).isEmpty()) {
			FailedMessageEntity failedMessageEntity = new FailedMessageEntity();
			failedMessageEntity.setCrBy(IDA);
			failedMessageEntity.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			failedMessageEntity.setId(id);
			failedMessageEntity.setTopic(eventModel.getTopic());
			failedMessageEntity.setPublishedOnDtimes(DateUtils.convertUTCToLocalDateTime(eventModel.getPublishedOn()));
			failedMessageEntity.setStatusCode(CredentialStoreStatus.NEW.name());
			failedMessageEntity.setFailedDTimes(failedDtimes);
			try {
				failedMessageEntity.setMessage(mapper.writeValueAsString(eventModel));
				failedMessagesRepo.save(failedMessageEntity);
			} catch (JsonProcessingException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), "storeEventModel",
						"error in json processing: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Update failed message.
	 *
	 * @param eventId the event id
	 * @param status the status
	 */
	public void updateFailedMessage(String eventId, String status) {
		FailedMessageEntity failedMessageEntity = failedMessagesRepo.getOne(eventId);
		failedMessageEntity.setUpdBy(IDA);
		failedMessageEntity.setUpdDTimes(DateUtils.getUTCCurrentDateTime());
		failedMessageEntity.setStatusCode(status);
		failedMessagesRepo.save(failedMessageEntity);
	}

	/**
	 * Process failed message.
	 *
	 * @param failedMessage the failed message
	 */
	private void processFailedMessage(FailedMessageEntity failedMessage) {
		try {
			doProcessFailedMessage(failedMessage);
			updateFailedMessage(failedMessage.getId(), MessageStoreStatus.PROCESSED.name());
		} catch (Exception e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), "processFailedMessage",
					"Error in Processing failedMessage : " + failedMessage.toString() + ": "
							+ ExceptionUtils.getStackTrace(e));
			updateFailedMessage(failedMessage.getId(), MessageStoreStatus.FAILED.name());
		}
	}

	/**
	 * Do process failed message.
	 *
	 * @param failedMessage the failed message
	 * @throws Exception the exception
	 */
	private void doProcessFailedMessage(FailedMessageEntity failedMessage) throws Exception {
		getFailedMessageConsumer(failedMessage.getTopic()).accept(failedMessage);
	}

	/**
	 * Gets the failed message consumer.
	 *
	 * @param topic the topic
	 * @return the failed message consumer
	 */
	private ConsumerWithThrowable<FailedMessageEntity, Exception> getFailedMessageConsumer(String topic) {
		if (topic.contains("/")) {
			String topicPrefix = authPartherId + "/";
			String authTypeStatusTopic = topicPrefix + IDAEventType.AUTH_TYPE_STATUS_UPDATE.name();
			if (topic.equalsIgnoreCase(authTypeStatusTopic)) {
				return this::processAuthTypeStatusEvent;
			} else {
				Optional<IDAEventType> eventType = getIdChangeEventType(topic, topicPrefix);
				if (eventType.isPresent()) {
					return failedMessage -> this.processIdChangeEvent(eventType.get(), failedMessage);
				}

				Optional<PartnerEventTypes> partnerType = getPartnerEventType(topic);
				if (partnerType.isPresent()) {
					return failedMessage -> this.processPartnerEvent(partnerType.get(), failedMessage);
				}
			}

		} else {
			if (topic.equalsIgnoreCase(hotlistEventTopic)) {
				return this::processHotlistEvent;
			} else if (topic.equalsIgnoreCase(masterdataTemplatesEventTopic)) {
				return this::processMasterdataTemplatesEvent;
			} else if (topic.equalsIgnoreCase(masterdataTitlesEventTopic)) {
				return this::processMasterdataTitlesEvent;
			} else if (topic.equalsIgnoreCase(partnerCertEventTopic)) {
				return this::processPartnerCACertEvent;
			}
		}
		return DUMMY_CONSUMER;
	}

	/**
	 * Gets the partner event type.
	 *
	 * @param topic the topic
	 * @return the partner event type
	 */
	private Optional<PartnerEventTypes> getPartnerEventType(String topic) {
		Optional<PartnerEventTypes> partnerType = Arrays.stream(PartnerEventTypes.values())
				.filter(type -> (env.getProperty(type.getTopicPropertyName())).equalsIgnoreCase(topic)).findAny();
		return partnerType;
	}

	/**
	 * Gets the id change event type.
	 *
	 * @param topic the topic
	 * @param topicPrefix the topic prefix
	 * @return the id change event type
	 */
	private Optional<IDAEventType> getIdChangeEventType(String topic, String topicPrefix) {
		return Arrays.stream(FailedWebsubMessagesReader.ID_CHANGE_EVENTS)
				.filter(type -> (topicPrefix + type.toString()).equalsIgnoreCase(topic)).findAny();
	}

	/**
	 * Process id change event.
	 *
	 * @param eventType     the event type
	 * @param failedMessage the failed message
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public void processIdChangeEvent(IDAEventType eventType, FailedMessageEntity failedMessage)
			throws IdAuthenticationBusinessException {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, "processIdChangeEvent", "",
				"handling " + eventType + " event for partnerId: " + authPartherId);
		Optional<EventModel> eventModel = getEventModel(failedMessage);
		if (eventModel.isPresent()) {
			credentialStoreService.handleIdEvent(eventModel.get());
		}
	}

	/**
	 * Process auth type status event.
	 *
	 * @param failedMessage the failed message
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	public void processAuthTypeStatusEvent(FailedMessageEntity failedMessage) throws IdAuthenticationAppException {
		try {
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, "processAuthTypeStatusEvent",
					this.getClass().getCanonicalName(),
					"handling updateAuthtypeStatus event for partnerId: " + authPartherId);
			Optional<EventModel> eventModelOpt = getEventModel(failedMessage);
			if (eventModelOpt.isPresent()) {
				EventModel eventModel = eventModelOpt.get();
				if (eventModel.getEvent() != null && eventModel.getEvent().getData() != null) {
					AuthTypeStatusEventDTO event = mapper.convertValue(eventModel.getEvent().getData(),
							AuthTypeStatusEventDTO.class);
					authtypeStatusService.updateAuthTypeStatus(event.getTokenId(), event.getAuthTypeStatusList());
				}
			}
		} catch (IdAuthenticationBusinessException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().getCanonicalName(), e.getErrorCode(),
					e.getErrorText());
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(),
					"getEventModel", "Error in Parsing message as EventModel : " + failedMessage.toString() + ": "
							+ ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * Process hotlist event.
	 *
	 * @param failedMessage the failed message
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public void processHotlistEvent(FailedMessageEntity failedMessage) throws IdAuthenticationBusinessException {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(), "processHotlistEvent",
				"EVENT RECEIVED");
		Optional<EventModel> eventModel = getEventModel(failedMessage);
		if (eventModel.isPresent()) {
			hotlistService.handlingHotlistingEvent(eventModel.get());
		}
	}

	/**
	 * Process master data templates event.
	 *
	 * @param failedMessage the failed message
	 */
	public void processMasterdataTemplatesEvent(FailedMessageEntity failedMessage) {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(),
				"processMasterdataTemplatesEvent", "EVENT RECEIVED");
		Optional<EventModel> eventModel = getEventModel(failedMessage);
		if (eventModel.isPresent()) {
			masterDataCacheUpdateService.updateTemplates(eventModel.get());
		}
	}

	/**
	 * Process master data titles event.
	 *
	 * @param failedMessage the failed message
	 */
	public void processMasterdataTitlesEvent(FailedMessageEntity failedMessage) {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(),
				"processMasterdataTitlesEvent", "EVENT RECEIVED");
		Optional<EventModel> eventModel = getEventModel(failedMessage);
		if (eventModel.isPresent()) {
			masterDataCacheUpdateService.updateTemplates(eventModel.get());
		}
	}

	/**
	 * Process partner CA cert event.
	 *
	 * @param failedMessage the failed message
	 * @throws RestServiceException              the rest service exception
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public void processPartnerCACertEvent(FailedMessageEntity failedMessage)
			throws RestServiceException, IdAuthenticationBusinessException {
		mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(),
				"processPartnerCACertEvent", "EVENT RECEIVED");
		Optional<EventModel> eventModel = getEventModel(failedMessage);
		if (eventModel.isPresent()) {
			partnerCACertEventService.handleCACertEvent(eventModel.get());
		}
	}

	/**
	 * Process partner event.
	 *
	 * @param partnerEventType the partner event type
	 * @param failedMessage    the failed message
	 */
	public void processPartnerEvent(PartnerEventTypes partnerEventType, FailedMessageEntity failedMessage) {
		try {
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(),
					"processPartnerEvent", partnerEventType.getName() + " EVENT RECEIVED");
			Optional<EventModel> eventModel = getEventModel(failedMessage);
			if (eventModel.isPresent()) {
				handlePartnerEvent(partnerEventType, eventModel.get());
			}
		} catch (Exception e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getCanonicalName(),
					"processPartnerEvent",
					StringUtils.arrayToDelimitedString(ExceptionUtils.getRootCauseStackTrace(e), "\n"));
		}
	}

	/**
	 * Gets the event model.
	 *
	 * @param failedMessage the failed message
	 * @return the event model
	 */
	private Optional<EventModel> getEventModel(FailedMessage failedMessage) {
		try {
			return Optional.of(mapper.readValue(failedMessage.getMessage(), EventModel.class));
		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), "getEventModel",
					"Error in Parsing message as EventModel : " + failedMessage.toString() + ": "
							+ ExceptionUtils.getStackTrace(e));
		}
		return Optional.empty();
	}

	/**
	 * Gets the event model.
	 *
	 * @param failedMessage the failed message
	 * @return the event model
	 */
	private Optional<EventModel> getEventModel(FailedMessageEntity failedMessage) {
		try {
			return Optional.of(mapper.readValue(failedMessage.getMessage(), EventModel.class));
		} catch (IOException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getName(), "getEventModel",
					"Error in Parsing message as EventModel : " + failedMessage.toString() + ": "
							+ ExceptionUtils.getStackTrace(e));
		}
		return Optional.empty();
	}

	/**
	 * Handle partner event.
	 *
	 * @param partnerEventType the partner event type
	 * @param eventModel the event model
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void handlePartnerEvent(PartnerEventTypes partnerEventType, EventModel eventModel)
			throws JsonParseException, JsonMappingException, IOException {
		switch (partnerEventType) {
		case API_KEY_APPROVED:
			partnerManager.handleApiKeyApproved(eventModel);
			break;
		case MISP_LIC_GENERATED:
		case MISP_LIC_UPDATED:
			partnerManager.updateMispLicenseData(eventModel);
			break;
		case PARTNER_UPDATED:
			partnerManager.updatePartnerData(eventModel);
			break;
		case PARTNER_API_KEY_UPDATED:
			partnerManager.handleApiKeyUpdated(eventModel);
			break;
		case POLICY_UPDATED:
			partnerManager.updatePolicyData(eventModel);
			break;
		default:
			break;
		}
	}

}
