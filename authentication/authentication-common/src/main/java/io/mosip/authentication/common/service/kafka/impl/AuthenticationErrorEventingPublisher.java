package io.mosip.authentication.common.service.kafka.impl;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTHENTICATION_ERROR_EVENTING_TOPIC;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.AUTHENTICATION_ERROR_EVENTING_ENABLED;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.PartnerData;
import io.mosip.authentication.common.service.repository.PartnerDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
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
 * The Class AuthenticationErrorEventingPublisher.
 * 
 * @author Neha
 */
@Component
@ConditionalOnProperty(value = AUTHENTICATION_ERROR_EVENTING_ENABLED, havingValue = "true", matchIfMissing = false)
public class AuthenticationErrorEventingPublisher {

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

	private static final Logger logger = IdaLogger.getLogger(AuthenticationErrorEventingPublisher.class);


	/** The Authenticatrion error eventing topic. */
	@Value("${" + AUTHENTICATION_ERROR_EVENTING_TOPIC + "}")
	private String authenticationErrorEventingTopic;
	
	@Value("${mosip.ida.authentication.error.eventing.encrypt.partner.id}")
	 private String partnerId;
	
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	private IdAuthSecurityManager securityManager;
	
	@Autowired
	private PartnerDataRepository partnerDataRepo;

	

	public void notify(BaseRequestDTO baserequestdto, String headerSignature, Optional<PartnerDTO> partner,
			IdAuthenticationBusinessException e, Map<String, Object> metadata) {
		try {
			sendEvents(baserequestdto, headerSignature, partner, e, metadata);
		} catch (Exception exception) {
			logger.error(IdRepoSecurityManager.getUser(), "Authentication error eventing", "notify",
					exception.getMessage());
		}
	}

	private void sendEvents(BaseRequestDTO baserequestdto, String headerSignature, Optional<PartnerDTO> partner,
			IdAuthenticationBusinessException e, Map<String, Object> metadata) {
		logger.info("Inside sendEvents authentication error eventing");
		logger.info("Inside partner data to get certificate for authentication error eventing encryption");
		Optional<PartnerData> partnerDataCert = partnerDataRepo.findByPartnerId(partnerId);
		if (partnerDataCert.isEmpty()) {
			logger.info("Partner is not configured for encrypting individual id.");
		} else {
			Map<String, Object> eventData = new HashMap<>();
			eventData.put(ERROR_CODE, e.getErrorCode());
			eventData.put(ERROR_MESSAGE, e.getErrorText());
			eventData.put(REQUESTDATETIME, DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
			eventData.put(INDIVIDUAL_ID,
					encryptIndividualId(baserequestdto.getIndividualId(), partnerDataCert.get().getCertificateData()));
			eventData.put(AUTH_PARTNER_ID, partner.map(PartnerDTO::getPartnerId).orElse(null));
			eventData.put(INDIVIDUAL_ID_TYPE, baserequestdto.getIndividualIdType());
			eventData.put(ENTITY_NAME, partner.map(PartnerDTO::getPartnerName).orElse(null));
			eventData.put(REQUEST_SIGNATURE, headerSignature);
			EventModel eventModel = createEventModel(authenticationErrorEventingTopic, eventData);
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
	
	public void publishEvent(EventModel eventModel) {
		kafkaTemplate.send(authenticationErrorEventingTopic, eventModel);
	}

	private String encryptIndividualId(String id, String partnerCertificate) {
		try {
			logger.info("Inside the method of encrypting IndividualId using partner certificate ");
			return securityManager.asymmetricEncryption(id.getBytes(), partnerCertificate);
		} catch (IdAuthenticationBusinessException e) {
			// TODO Auto-generated catch block
			logger.error("Error occurred during encryption of individual ID", e);
		}
		return null;

	}

}