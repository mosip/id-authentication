package io.mosip.registration.processor.stages.uigenerator;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;


/**
 * The Class UinGeneratorStage.
 * @author M1047487
 */
@Service
public class UinGeneratorStage extends MosipVerticleManager {

	/** The Constant LOGGER. */
	//private static final Logger LOGGER = LoggerFactory.getLogger(UinGeneratorStageTest.class);

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The Constant LOGDISPLAY. */
	private static final String LOGDISPLAY = "{} - {}";

	/** The secs. */
	// @Value("${landingzone.scanner.stage.time.interval}")
	private long secs = 30;

	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;
	
	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	@Value("${registration.processor.vertx.localhost}")
	private String localhost;
	
	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;



	/** The registration id. */
	private String registrationId = "";

	@Override
	public MessageDTO process(MessageDTO object) {
		this.registrationId = object.getRid();
		System.out.println(this.registrationId);
		return null;
	}

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.UIN_GENERATION_BUS_IN, MessageBusAddress.UIN_GENERATION_BUS_OUT);

	}

}
