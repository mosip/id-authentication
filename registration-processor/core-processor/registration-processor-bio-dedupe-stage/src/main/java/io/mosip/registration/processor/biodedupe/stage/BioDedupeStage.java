/**
 * 
 */
package io.mosip.registration.processor.biodedupe.stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

/**
 * The Class BioDedupeStage.
 *
 * @author Sowmya
 */
@Service
public class BioDedupeStage extends MosipVerticleManager {

	/** The cluster manager url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	@Autowired
	BioDedupeProcessor bioDedupeProcessor;

	/** The Constant INTERNAL_ERROR. */
	private static final String INTERNAL_ERROR = "Internal error occurred in bio-dedupe stage while processing for registrationId ";

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.BIO_DEDUPE_BUS_IN, MessageBusAddress.BIO_DEDUPE_BUS_OUT);
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
		return bioDedupeProcessor.process(object, this.getClass().getSimpleName());
	}

}
