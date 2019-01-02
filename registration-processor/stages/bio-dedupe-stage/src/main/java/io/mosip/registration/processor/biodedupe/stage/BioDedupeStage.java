/**
 * 
 */
package io.mosip.registration.processor.biodedupe.stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * @author M1022006
 *
 */
@RefreshScope
@Service
public class BioDedupeStage extends MosipVerticleManager {
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(BioDedupeStage.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Autowired
	private BasePacketRepository<ManualVerificationEntity, String> manualVerficationRepository;

	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.BIODEDUPE_BUS_IN, MessageBusAddress.BIODEDUPE_BUS_OUT);
	}

	@Override
	public MessageDTO process(MessageDTO object) {

		return null;
	}

}
