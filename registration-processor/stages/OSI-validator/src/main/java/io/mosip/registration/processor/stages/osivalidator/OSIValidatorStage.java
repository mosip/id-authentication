package io.mosip.registration.processor.stages.osivalidator;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.stages.osivalidator.exception.utils.ExceptionMessages;
import io.mosip.registration.processor.stages.osivalidator.utils.StatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@RefreshScope
@Service
public class OSIValidatorStage extends MosipVerticleManager {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(OSIValidatorStage.class);

	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	FilesystemCephAdapterImpl adapter;

	@Autowired
	RegistrationProcessorRestClientService<Object> restClientService;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterAddress, localhost);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.OSI_BUS_IN, MessageBusAddress.OSI_BUS_OUT);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		object.setMessageBusAddress(MessageBusAddress.OSI_BUS_IN);
		object.setIsValid(Boolean.FALSE);
		object.setInternalError(Boolean.FALSE);

		String registrationId = object.getRid();
		boolean isValidOSI = false;
		InternalRegistrationStatusDto registrationStatusDto = registrationStatusService
				.getRegistrationStatus(registrationId);
		OSIValidator osiValidator = new OSIValidator(adapter, restClientService);
		osiValidator.registrationStatusDto = registrationStatusDto;

		try {

			isValidOSI = osiValidator.isValidOSI(registrationId);

		} catch (IOException e) {
			log.error(ExceptionMessages.OSI_VALIDATION_FAILED.name(), e);
			object.setInternalError(Boolean.TRUE);

		} catch (Exception ex) {
			log.error(ExceptionMessages.OSI_VALIDATION_FAILED.name(), ex);
			object.setInternalError(Boolean.TRUE);

		}

		if (isValidOSI) {
			object.setIsValid(Boolean.TRUE);
			registrationStatusDto.setStatusComment(StatusMessage.OSI_VALIDATION_SUCCESS);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_OSI_VALIDATION_SUCCESSFUL.toString());
		} else {
			object.setIsValid(Boolean.FALSE);
			if (registrationStatusDto.getRetryCount() == null) {
				registrationStatusDto.setRetryCount(0);
			} else {
				registrationStatusDto.setRetryCount(registrationStatusDto.getRetryCount() + 1);
			}
			registrationStatusDto.setStatusComment(osiValidator.registrationStatusDto.getStatusComment());
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PACKET_OSI_VALIDATION_FAILED.toString());
		}
		registrationStatusService.updateRegistrationStatus(registrationStatusDto);
		return object;
	}

}
