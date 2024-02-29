package io.mosip.authentication.common.service.websub.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.ON_DEMAND_TEMPLATE_EXTRACTION_TOPIC;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.PartnerData;
import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.repository.PartnerDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.BaseRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;

/**
 * The Class OnDemandTemplateEventPublisher.
 * 
 * @author Neha
 */
@Component
public class OndemandTemplateEventPublisher extends BaseWebSubEventsInitializer {

	private static final String REQUEST_SIGNATURE = "requestSignature";

	private static final String ENTITY_NAME = "entityName";

	private static final String INDIVIDUAL_ID_TYPE = "individualIdType";

	private static final String AUTH_PARTNER_ID = "authPartnerId";

	private static final String INDIVIDUAL_ID = "individualId";

	private static final String REQUESTDATETIME = "requestdatetime";

	private static final String ERROR_MESSAGE = "error_message";

	private static final String ERROR_CODE = "error_Code";

	/** The Constant PUBLISHER_IDA. */
	private static final String PUBLISHER_IDA = "IDA";

	/** The Constant logger. */

	private static final Logger logger = IdaLogger.getLogger(OndemandTemplateEventPublisher.class);


	/** The on demand template extraction topic. */
	@Value("${" + ON_DEMAND_TEMPLATE_EXTRACTION_TOPIC + "}")
	private String onDemadTemplateExtractionTopic;
	
	@Value("${mosip.ida.ondemand.template.extraction.partner.id}")
	 private String partnerId;

	/** The web sub event publish helper. */
	@Autowired
	private WebSubHelper webSubHelper;

	@Autowired
	private IdAuthSecurityManager securityManager;
	
	@Autowired
	private PartnerDataRepository partnerDataRepo;

	/**
	 * Do subscribe.
	 */
	@Override
	protected void doSubscribe() {
		// Nothing to do here since we are just publishing event for this topic
	}

	/**
	 * Try register topic partner service events.
	 */
	private void tryRegisterTopicOnDemandEvent() {
		try {
			logger.debug(IdAuthCommonConstants.SESSION_ID, "tryRegisterOnDemandEvent", "",
					"Trying to register topic: " + onDemadTemplateExtractionTopic);
			webSubHelper.registerTopic(onDemadTemplateExtractionTopic);
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterOnDemandEvent", "",
					"Registered topic: " + onDemadTemplateExtractionTopic);
		} catch (Exception e) {
			logger.info(IdAuthCommonConstants.SESSION_ID, "tryRegisterOnDemandEvent", e.getClass().toString(),
					"Error registering topic: " + onDemadTemplateExtractionTopic + "\n" + e.getMessage());
		}
	}

	@Override
	protected void doRegister() {
		logger.info(IdAuthCommonConstants.SESSION_ID, "doRegister", this.getClass().getSimpleName(),
				"On demand template event topic..");
		tryRegisterTopicOnDemandEvent();
	}

	public void publishEvent(EventModel eventModel) {
		webSubHelper.publishEvent(onDemadTemplateExtractionTopic, eventModel);
	}

	public void notify(BaseRequestDTO baserequestdto, String headerSignature, Optional<PartnerDTO> partner,
			IdAuthenticationBusinessException e, Map<String, Object> metadata) {
		try {
			sendEvents(baserequestdto, headerSignature, partner, e, metadata);
		} catch (Exception exception) {
			logger.error(IdRepoSecurityManager.getUser(), "On demand template  extraction", "notify",
					exception.getMessage());
		}
	}

	private void sendEvents(BaseRequestDTO baserequestdto, String headerSignature, Optional<PartnerDTO> partner,
			IdAuthenticationBusinessException e, Map<String, Object> metadata) {
		logger.info("Inside sendEvents ondemand extraction");
		logger.info("Inside partner data to get certificate for ondemand extraction encryption");
		Optional<PartnerData> partnerDataCert = partnerDataRepo.findByPartnerId(partnerId);
		if (partnerDataCert.isEmpty()) {
			logger.info("Partner is not configured for on demand extraction.");
		} else {
			Map<String, Object> eventData = new HashMap<>();
			eventData.put(ERROR_CODE, e.getErrorCode());
			eventData.put(ERROR_MESSAGE, e.getErrorText());
			eventData.put(REQUESTDATETIME, DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
			eventData.put(INDIVIDUAL_ID,
					encryptIndividualId(baserequestdto.getIndividualId(), partnerDataCert.get().getCertificateData()));
			eventData.put(AUTH_PARTNER_ID, partner.get().getPartnerId());
			eventData.put(INDIVIDUAL_ID_TYPE, baserequestdto.getIndividualIdType());
			eventData.put(ENTITY_NAME, partner.get().getPartnerName());
			eventData.put(REQUEST_SIGNATURE, headerSignature);
			EventModel eventModel = createEventModel(onDemadTemplateExtractionTopic, eventData);
			publishEvent(eventModel);
		}
	}

	private EventModel createEventModel(String topic, Map<String, Object> eventData) {
		EventModel model = new EventModel();
		model.setPublisher(PUBLISHER_IDA);
		String dateTime = DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime());
		model.setPublishedOn(dateTime);
		Event event = new Event();
		event.setTimestamp(dateTime);
		String eventId = UUID.randomUUID().toString();
		event.setId(eventId);
		event.setData(eventData);
		model.setEvent(event);
		model.setTopic(topic);
		return model;
	}

	private byte[] encryptIndividualId(String id, String partnerCertificate) {
		try {
			logger.info("Inside the method of encryptIndividualId using partner certificate ");
			return securityManager.asymmetricEncryption(id.getBytes(), partnerCertificate);
		} catch (IdAuthenticationBusinessException e) {
			// TODO Auto-generated catch block
			logger.error("Error occurred during encryption of individual ID", e);
		}
		return null;

	}

}