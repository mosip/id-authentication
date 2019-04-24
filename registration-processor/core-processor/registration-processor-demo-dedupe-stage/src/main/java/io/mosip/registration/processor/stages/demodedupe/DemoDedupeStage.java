package io.mosip.registration.processor.stages.demodedupe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

/**
 * The Class DemodedupeStage.
 *
 * @author M1048358 Alok Ranjan
 */

@RefreshScope
@Service
public class DemoDedupeStage extends MosipVerticleManager {

	/** The reg proc logger. */

	/** The cluster address. */
	@Value("${registration.processor.vertx.cluster.address}")
	private String clusterAddress;

	/** The localhost. */
	@Value("${registration.processor.vertx.localhost}")
	private String localhost;

	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	@Autowired
	DemodedupeProcessor demodedupeProcessor;

	private static final String INTERNAL_occurred = "Internal error occurred in demo dedupe stage while processing registrationId ";

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.DEMO_DEDUPE_BUS_IN, MessageBusAddress.DEMO_DEDUPE_BUS_OUT);
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
		return demodedupeProcessor.process(object, this.getClass().getSimpleName());

	}

}