package io.mosip.registration.processor.stages.osivalidator;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.stages.osivalidator.utils.StatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * The Class OSIValidatorStage.
 */
@RefreshScope
@Service
public class OSIValidatorStage extends MosipVerticleManager {

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(OSIValidatorStage.class);

	/** The cluster address. */
	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	/** The localhost. */
	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The audit log request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The osi validator. */
	@Autowired
	OSIValidator osiValidator;

	/** The umc validator. */
	@Autowired
	UMCValidator umcValidator;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.OSI_BUS_IN, MessageBusAddress.OSI_BUS_OUT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {

		object.setMessageBusAddress(MessageBusAddress.OSI_BUS_IN);
		object.setIsValid(Boolean.FALSE);
		object.setInternalError(Boolean.FALSE);
		String description = "";
		boolean isTransactionSuccessful = false;
		boolean isValidUMC = false;
		String registrationId = object.getRid();
		boolean isValidOSI = false;
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);

		osiValidator.registrationStatusDto = registrationStatusDto;
		umcValidator.setRegistrationStatusDto(registrationStatusDto);
		try {
			isValidUMC = umcValidator.isValidUMC(registrationId);
			if (isValidUMC) {
				isValidOSI = osiValidator.isValidOSI(registrationId);
			}
			if (isValidUMC && isValidOSI) {
				object.setIsValid(Boolean.TRUE);
				registrationStatusDto.setStatusComment(StatusMessage.OSI_VALIDATION_SUCCESS);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_OSI_VALIDATION_SUCCESSFUL.toString());
				isTransactionSuccessful = true;
				description = "OSI validation successful for registration id : " + registrationId;
			} else {
				object.setIsValid(Boolean.FALSE);
				int retryCount = registrationStatusDto.getRetryCount() != null
						? registrationStatusDto.getRetryCount() + 1
						: 1;
				registrationStatusDto.setRetryCount(retryCount);

				registrationStatusDto.setStatusComment(osiValidator.registrationStatusDto.getStatusComment());
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_OSI_VALIDATION_FAILED.toString());

				description = "OSI validation Failed for registration id : " + registrationId;
			}
			registrationStatusDto.setUpdatedBy(USER);
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);

		} catch (DataAccessException e) {
			log.error(PlatformErrorMessages.OSI_VALIDATION_FAILED.name(), e);
			object.setInternalError(Boolean.TRUE);
			description = "Data voilation in reg packet : " + registrationId;

		} catch (IOException | ApisResourceAccessException e) {
			log.error(PlatformErrorMessages.OSI_VALIDATION_FAILED.name(), e);
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;

		} catch (Exception ex) {
			log.error(PlatformErrorMessages.OSI_VALIDATION_FAILED.name(), ex);
			object.setInternalError(Boolean.TRUE);
			description = "Internal error occured while processing registration  id : " + registrationId;
		} finally {

			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);

		}

		return object;
	}

}
